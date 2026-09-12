package com.airroutex.algorithms;

import com.airroutex.models.Flight;

import java.util.List;

/**
 * GraphBuilder.java
 * ------------------------------------------------------------------
 * Builds a Graph from a List<Flight>. This is the ONLY bridge
 * between "data that came from MySQL" and "the graph algorithms" -
 * it takes plain Flight objects (already fetched by FlightDAO) and
 * turns them into a Graph, keeping the algorithm layer completely
 * decoupled from JDBC.
 *
 *   MySQL -> FlightDAO (JDBC) -> List<Flight> -> GraphBuilder -> Graph
 *                                                                   |
 *                                                                   v
 *                                          BFSPathFinder / DijkstraPathFinder
 *
 * Time Complexity:  O(F) where F = number of flights (each flight
 *                    becomes exactly one addEdge call).
 * Space Complexity: O(V + F) for the adjacency list.
 * ------------------------------------------------------------------
 */
public class GraphBuilder {

    private GraphBuilder() {
        // utility class - no instances
    }

    public static Graph buildGraph(List<Flight> flights) {
        Graph graph = new Graph();
        for (Flight flight : flights) {
            graph.addEdge(flight);
        }
        return graph;
    }
}
