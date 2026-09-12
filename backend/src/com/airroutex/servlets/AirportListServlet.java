package com.airroutex.servlets;

import com.airroutex.dao.AirportDAO;
import com.airroutex.models.Airport;
import com.airroutex.utils.JsonArray;
import com.airroutex.utils.JsonObject;
import com.airroutex.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * AirportListServlet.java
 * ------------------------------------------------------------------
 * GET /AirRouteX/airports
 * Requires login (any role) - used to populate the source/destination
 * dropdowns on the Search Flights page.
 * ------------------------------------------------------------------
 */
@WebServlet("/airports")
public class AirportListServlet extends HttpServlet {

    private final AirportDAO airportDAO = new AirportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!SessionUtil.isLoggedIn(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print(new JsonObject().put("error", "Please log in first").toJson());
            }
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            List<Airport> airports = airportDAO.getAllAirports();
            JsonArray json = new JsonArray();
            for (Airport a : airports) {
                json.add(JsonMappers.airport(a));
            }
            out.print(json.toJson());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.print(new JsonObject().put("error", "Database error: " + e.getMessage()).toJson());
            }
        }
    }
}
