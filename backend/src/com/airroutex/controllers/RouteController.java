package com.airroutex.controllers;

import com.airroutex.algorithms.BFSPathFinder;
import com.airroutex.algorithms.DijkstraPathFinder;
import com.airroutex.algorithms.Graph;
import com.airroutex.algorithms.GraphBuilder;
import com.airroutex.algorithms.WeightType;
import com.airroutex.dao.AirportDAO;
import com.airroutex.dao.FlightDAO;
import com.airroutex.models.Airport;
import com.airroutex.models.Flight;
import com.airroutex.models.RouteResult;

import java.sql.SQLException;
import java.util.List;

/**
 * RouteController.java
 * ------------------------------------------------------------------
 * Controller/Service layer that ties everything together for a
 * route search:
 *
 *   1. Resolve the source/destination airport CODES (e.g. "DEL") the
 *      user typed into airport_id's (via AirportDAO).
 *   2. Read ALL flights from MySQL (via FlightDAO).
 *   3. Build a fresh in-memory Graph from those flights
 *      (via GraphBuilder) - the graph is NEVER cached, so admin
 *      edits are reflected on the very next search.
 *   4. Run the correct algorithm based on the requested optimization
 *      type: BFS for "layovers", Dijkstra (with the matching
 *      WeightType) for "distance"/"price"/"duration".
 *
 * This class is intentionally the ONLY place that knows both the DAO
 * layer and the algorithm layer exist - Graph/BFS/Dijkstra never
 * import anything from dao/, and AirportDAO/FlightDAO never import
 * anything from algorithms/, keeping the two layers independent as
 * required.
 * ------------------------------------------------------------------
 */
public class RouteController {

    public static final String OPTIMIZE_DISTANCE = "distance";
    public static final String OPTIMIZE_PRICE = "price";
    public static final String OPTIMIZE_DURATION = "duration";
    public static final String OPTIMIZE_LAYOVERS = "layovers";

    private final AirportDAO airportDAO = new AirportDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private final BFSPathFinder bfsPathFinder = new BFSPathFinder();
    private final DijkstraPathFinder dijkstraPathFinder = new DijkstraPathFinder();

    public static class SearchResult {
        public final boolean success;
        public final String errorMessage;
        public final RouteResult routeResult;

        private SearchResult(boolean success, String errorMessage, RouteResult routeResult) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.routeResult = routeResult;
        }

        static SearchResult ok(RouteResult routeResult) {
            return new SearchResult(true, null, routeResult);
        }

        static SearchResult fail(String message) {
            return new SearchResult(false, message, null);
        }
    }

    public SearchResult search(String sourceCode, String destinationCode, String optimizationType) {
        try {
            if (sourceCode == null || destinationCode == null || optimizationType == null) {
                return SearchResult.fail("source, destination, and optimizationType are required");
            }
            if (sourceCode.equalsIgnoreCase(destinationCode)) {
                return SearchResult.fail("Source and destination airports cannot be the same");
            }

            Airport source = airportDAO.getAirportByCode(sourceCode);
            Airport destination = airportDAO.getAirportByCode(destinationCode);
            if (source == null || destination == null) {
                return SearchResult.fail("Unknown airport code");
            }

            List<Flight> allFlights = flightDAO.getAllFlights();
            Graph graph = GraphBuilder.buildGraph(allFlights);

            RouteResult routeResult;
            switch (optimizationType.toLowerCase()) {
                case OPTIMIZE_LAYOVERS:
                    routeResult = bfsPathFinder.findRoute(graph, source.getAirportId(), destination.getAirportId());
                    break;
                case OPTIMIZE_DISTANCE:
                    routeResult = dijkstraPathFinder.findRoute(graph, source.getAirportId(), destination.getAirportId(), WeightType.DISTANCE);
                    break;
                case OPTIMIZE_PRICE:
                    routeResult = dijkstraPathFinder.findRoute(graph, source.getAirportId(), destination.getAirportId(), WeightType.PRICE);
                    break;
                case OPTIMIZE_DURATION:
                    routeResult = dijkstraPathFinder.findRoute(graph, source.getAirportId(), destination.getAirportId(), WeightType.DURATION);
                    break;
                default:
                    return SearchResult.fail("optimizationType must be one of: distance, price, duration, layovers");
            }

            if (!routeResult.isFound()) {
                return SearchResult.fail("No route found between " + sourceCode + " and " + destinationCode);
            }

            return SearchResult.ok(routeResult);

        } catch (SQLException e) {
            return SearchResult.fail("A database error occurred while searching: " + e.getMessage());
        }
    }
}
