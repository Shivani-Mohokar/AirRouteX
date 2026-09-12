package com.airroutex.algorithms;

import com.airroutex.models.Flight;
import com.airroutex.models.RouteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * BFSPathFinder.java
 * ------------------------------------------------------------------
 * Breadth-First Search - finds the route with the MINIMUM NUMBER OF
 * LAYOVERS (fewest flights / edges), completely ignoring distance,
 * price, and duration.
 *
 * Why BFS instead of Dijkstra here: if every flight counted as
 * "weight 1", Dijkstra would still get the right answer, but that's
 * needless overhead (a priority queue with O(log V) operations) for
 * a problem that a plain FIFO Queue solves in O(V + E). BFS explores
 * the graph level by level (1 flight away, then 2 flights away, ...),
 * so the FIRST time the destination is dequeued, that path is
 * guaranteed to use the fewest possible flights.
 *
 * Why java.util.Queue (LinkedList): a FIFO queue is exactly what
 * "process nodes in the order they were discovered" requires -
 * enqueue new airports as they're found, dequeue the oldest one next.
 *
 * Time Complexity:  O(V + E) - every airport and flight is visited
 *                    at most once.
 * Space Complexity: O(V) - for the queue, visited set, and the
 *                    previous-edge map used to rebuild the path.
 * ------------------------------------------------------------------
 */
public class BFSPathFinder {

    public RouteResult findRoute(Graph graph, int sourceAirportId, int destinationAirportId) {

        if (!graph.hasVertex(sourceAirportId) || !graph.hasVertex(destinationAirportId)) {
            return RouteResult.notFound();
        }

        Set<Integer> visited = new HashSet<>();
        Map<Integer, Flight> previousEdge = new HashMap<>(); // airport_id -> flight edge used to reach it
        Queue<Integer> queue = new LinkedList<>();

        visited.add(sourceAirportId);
        queue.add(sourceAirportId);

        boolean found = false;

        while (!queue.isEmpty()) {
            int currentAirportId = queue.poll();

            if (currentAirportId == destinationAirportId) {
                found = true;
                break;
            }

            for (Flight edge : graph.getNeighbors(currentAirportId)) {
                int neighborId = edge.getDestinationAirportId();
                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    previousEdge.put(neighborId, edge);
                    queue.add(neighborId);
                }
            }
        }

        if (!found) {
            return RouteResult.notFound();
        }

        return buildRouteResult(sourceAirportId, destinationAirportId, previousEdge);
    }

    /** Walks the previousEdge map backward from destination to source to rebuild the path. */
    private RouteResult buildRouteResult(int sourceAirportId, int destinationAirportId,
                                          Map<Integer, Flight> previousEdge) {
        List<Flight> pathFlights = new ArrayList<>();
        int current = destinationAirportId;

        while (current != sourceAirportId) {
            Flight edge = previousEdge.get(current);
            pathFlights.add(0, edge);
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
