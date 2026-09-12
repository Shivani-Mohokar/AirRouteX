package com.airroutex.algorithms;

import com.airroutex.models.Flight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph.java
 * ------------------------------------------------------------------
 * A directed, weighted graph representing the airline route network.
 *
 *   VERTEX = an airport (identified by its airport_id, an int)
 *   EDGE   = a Flight, directed from Flight.sourceAirportId to
 *            Flight.destinationAirportId, carrying THREE possible
 *            weights (distanceKm, price, durationMinutes)
 *
 * Implemented as an ADJACENCY LIST using a java.util.HashMap where
 * the key is an airport_id and the value is an ArrayList of Flight
 * edges leaving that airport. This is the standard representation
 * for sparse graphs (an airline network is sparse - most airport
 * pairs do NOT have a direct flight), giving O(1) average lookup
 * of "what flights leave this airport" and O(V + E) total space.
 *
 * IMPORTANT: this class knows NOTHING about JDBC or MySQL. It is
 * built entirely in memory from a List<Flight> (see GraphBuilder),
 * which keeps the algorithm layer independent of the database layer
 * as required.
 * ------------------------------------------------------------------
 */
public class Graph {

    // adjacency list: airport_id -> list of flights departing from it
    private final Map<Integer, List<Flight>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    /** Ensures a vertex exists even if it has no outgoing flights yet. */
    public void addVertex(int airportId) {
        adjacencyList.putIfAbsent(airportId, new ArrayList<>());
    }

    /** Adds a directed edge (flight) sourceAirportId -> destinationAirportId. */
    public void addEdge(Flight flight) {
        addVertex(flight.getSourceAirportId());
        addVertex(flight.getDestinationAirportId());
        adjacencyList.get(flight.getSourceAirportId()).add(flight);
    }

    /** Returns all outgoing flights from the given airport - O(1) lookup. */
    public List<Flight> getNeighbors(int airportId) {
        return adjacencyList.getOrDefault(airportId, new ArrayList<>());
    }

    public boolean hasVertex(int airportId) {
        return adjacencyList.containsKey(airportId);
    }

    public Set<Integer> getAllVertices() {
        return adjacencyList.keySet();
    }

    public int vertexCount() {
        return adjacencyList.size();
    }
}
