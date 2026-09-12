package com.airroutex.algorithms;

import com.airroutex.models.Flight;
import com.airroutex.models.RouteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * DijkstraPathFinder.java
 * ------------------------------------------------------------------
 * Dijkstra's Shortest Path Algorithm - ONE implementation, reused
 * for THREE optimization types by changing which Flight field is
 * read as the edge weight (see WeightType):
 *
 *   WeightType.DISTANCE -> Shortest Distance route
 *   WeightType.PRICE    -> Lowest Ticket Price route
 *   WeightType.DURATION -> Shortest Duration route
 *
 * Why Dijkstra: all three weights (distance, price, duration) are
 * always non-negative for a real flight, so Dijkstra's greedy
 * assumption holds and it finds the true optimum. (This is also
 * exactly why Bellman-Ford is NOT used in this project - it exists
 * to handle negative weights, which never occur here, and it would
 * only be slower for no benefit.)
 *
 * Why java.util.PriorityQueue: Dijkstra repeatedly needs "the
 * unvisited airport with the smallest known distance so far". A
 * binary heap (which is exactly what PriorityQueue is backed by)
 * gives O(log V) insert/extract instead of an O(V) linear scan,
 * which is what makes Dijkstra run in O((V + E) log V) instead of
 * O(V^2).
 *
 * Time Complexity:  O((V + E) log V)
 * Space Complexity: O(V + E)  (adjacency list, distance map,
 *                    previous-edge map, and the priority queue)
 * ------------------------------------------------------------------
 */
public class DijkstraPathFinder {

    /** One entry in the priority queue: an airport and its current best-known distance. */
    private static class NodeDistance implements Comparable<NodeDistance> {
        final int airportId;
        final double distance;

        NodeDistance(int airportId, double distance) {
            this.airportId = airportId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * Runs Dijkstra from sourceAirportId to destinationAirportId using
     * the given weightType to decide which Flight field to minimize.
     */
    public RouteResult findRoute(Graph graph, int sourceAirportId, int destinationAirportId, WeightType weightType) {

        if (!graph.hasVertex(sourceAirportId) || !graph.hasVertex(destinationAirportId)) {
            return RouteResult.notFound();
        }

        Map<Integer, Double> distances = new HashMap<>();      // airport_id -> best known distance
        Map<Integer, Flight> previousEdge = new HashMap<>();   // airport_id -> flight edge used to reach it
        Map<Integer, Boolean> visited = new HashMap<>();       // airport_id -> finalized?

        for (int airportId : graph.getAllVertices()) {
            distances.put(airportId, Double.POSITIVE_INFINITY);
        }
        distances.put(sourceAirportId, 0.0);

        PriorityQueue<NodeDistance> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new NodeDistance(sourceAirportId, 0.0));

        while (!priorityQueue.isEmpty()) {
            NodeDistance current = priorityQueue.poll();

            if (Boolean.TRUE.equals(visited.get(current.airportId))) {
                continue; // stale queue entry (a shorter path to this node was already finalized)
            }
            visited.put(current.airportId, true);

            if (current.airportId == destinationAirportId) {
                break; // shortest path to the destination is finalized
            }

            for (Flight edge : graph.getNeighbors(current.airportId)) {
                int neighborId = edge.getDestinationAirportId();
                if (Boolean.TRUE.equals(visited.get(neighborId))) {
                    continue;
                }

                double edgeWeight = weightOf(edge, weightType);
                double newDistance = distances.get(current.airportId) + edgeWeight;

                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previousEdge.put(neighborId, edge);
                    priorityQueue.add(new NodeDistance(neighborId, newDistance));
                }
            }
        }

        if (distances.get(destinationAirportId) == null
                || distances.get(destinationAirportId) == Double.POSITIVE_INFINITY) {
            return RouteResult.notFound();
        }

        return buildRouteResult(sourceAirportId, destinationAirportId, previousEdge);
    }

    /** Returns the correct Flight field to use as the edge weight. */
    private double weightOf(Flight edge, WeightType weightType) {
        switch (weightType) {
            case DISTANCE:
                return edge.getDistanceKm();
            case PRICE:
                return edge.getPrice();
            case DURATION:
                return edge.getDurationMinutes();
            default:
                throw new IllegalArgumentException("Unknown weight type: " + weightType);
        }
    }

    /** Walks the previousEdge map backward from destination to source to rebuild the path. */
    private RouteResult buildRouteResult(int sourceAirportId, int destinationAirportId,
                                          Map<Integer, Flight> previousEdge) {
        List<Flight> pathFlights = new ArrayList<>();
        int current = destinationAirportId;

        while (current != sourceAirportId) {
            Flight edge = previousEdge.get(current);
            pathFlights.add(0, edge); // prepend so the final list is source -> ... -> destination
            current = edge.getSourceAirportId();
        }

        RouteResult result = new RouteResult();
        result.setFound(true);
        result.setFlights(pathFlights);

        double totalDistance = 0;
        double totalPrice = 0;
        int totalDuration = 0;
        for (Flight f : pathFlights) {
            totalDistance += f.getDistanceKm();
            totalPrice += f.getPrice();
            totalDuration += f.getDurationMinutes();
        }

        result.setTotalDistanceKm(totalDistance);
        result.setTotalPrice(totalPrice);
        result.setTotalDurationMinutes(totalDuration);
        result.setNumberOfLayovers(Math.max(0, pathFlights.size() - 1));

        return result;
    }
}
