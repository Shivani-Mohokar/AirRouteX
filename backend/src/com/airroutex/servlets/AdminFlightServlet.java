package com.airroutex.servlets;

import com.airroutex.controllers.AdminController;
import com.airroutex.models.Flight;
import com.airroutex.utils.JsonArray;
import com.airroutex.utils.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminFlightServlet.java
 * ------------------------------------------------------------------
 * GET    /AirRouteX/admin/flights           -> list all flights
 * POST   /AirRouteX/admin/flights           -> add a flight
 * PUT    /AirRouteX/admin/flights?id=5      -> update a flight
 * DELETE /AirRouteX/admin/flights?id=5      -> delete a flight
 *
 * Every flight added/edited/deleted here is exactly one EDGE in the
 * graph - the next search will read the updated Flight table and
 * rebuild the graph from scratch, so changes take effect immediately
 * with no server restart or cache invalidation needed.
 *
 * PUT requests don't get getParameter() populated by the servlet
 * container the way POST form submissions do, so this servlet reads
 * the PUT body manually and parses it as "key=value&key2=value2"
 * (the same encoding the frontend's fetch() sends for POST too).
 * ------------------------------------------------------------------
 */
@WebServlet("/admin/flights")
public class AdminFlightServlet extends AdminServletBase {

    private final AdminController adminController = new AdminController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            List<Flight> flights = adminController.listFlights();
            JsonArray json = new JsonArray();
            for (Flight f : flights) {
                json.add(JsonMappers.flight(f));
            }
            out.print(json.toJson());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print(new JsonObject().put("error", "Database error: " + e.getMessage()).toJson());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            AdminController.Result result = adminController.addFlight(
                    request.getParameter("flightNumber"),
                    parseInt(request.getParameter("airlineId")),
                    parseInt(request.getParameter("sourceAirportId")),
                    parseInt(request.getParameter("destinationAirportId")),
                    parseDouble(request.getParameter("distanceKm")),
                    parseDouble(request.getParameter("price")),
                    parseInt(request.getParameter("durationMinutes")),
                    request.getParameter("departureTime"),
                    request.getParameter("arrivalTime"));

            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }
            out.print(new JsonObject().put("flightId", (Integer) result.data).toJson());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", "id query parameter is required").toJson());
                return;
            }

            Map<String, String> body = parseUrlEncodedBody(request);

            AdminController.Result result = adminController.updateFlight(
                    Integer.parseInt(idParam),
                    body.get("flightNumber"),
                    parseInt(body.get("airlineId")),
                    parseInt(body.get("sourceAirportId")),
                    parseInt(body.get("destinationAirportId")),
                    parseDouble(body.get("distanceKm")),
                    parseDouble(body.get("price")),
                    parseInt(body.get("durationMinutes")),
                    body.get("departureTime"),
                    body.get("arrivalTime"));

            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }
            out.print(new JsonObject().put("message", "Flight updated").toJson());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", "id is required").toJson());
                return;
            }

            AdminController.Result result = adminController.deleteFlight(Integer.parseInt(idParam));
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }
            out.print(new JsonObject().put("message", "Flight deleted").toJson());
        }
    }

    /** Reads a PUT request body of the form "key=value&key2=value2" into a Map. */
    private Map<String, String> parseUrlEncodedBody(HttpServletRequest request) throws IOException {
        Map<String, String> result = new HashMap<>();
        StringBuilder body = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        for (String pair : body.toString().split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            String[] parts = pair.split("=", 2);
            String key = java.net.URLDecoder.decode(parts[0], "UTF-8");
            String value = parts.length > 1 ? java.net.URLDecoder.decode(parts[1], "UTF-8") : "";
            result.put(key, value);
        }
        return result;
    }

    private int parseInt(String s) {
        return s == null || s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    private double parseDouble(String s) {
        return s == null || s.isEmpty() ? 0 : Double.parseDouble(s);
    }
}
