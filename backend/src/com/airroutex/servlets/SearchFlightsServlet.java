package com.airroutex.servlets;

import com.airroutex.controllers.RouteController;
import com.airroutex.utils.JsonObject;
import com.airroutex.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * SearchFlightsServlet.java
 * ------------------------------------------------------------------
 * GET /AirRouteX/search?source=DEL&destination=BLR&optimizationType=price
 *
 * Requires login. Delegates entirely to RouteController, which reads
 * the current flight data from MySQL, builds the graph fresh, and
 * runs BFS or Dijkstra depending on optimizationType.
 * ------------------------------------------------------------------
 */
@WebServlet("/search")
public class SearchFlightsServlet extends HttpServlet {

    private final RouteController routeController = new RouteController();

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

        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String optimizationType = request.getParameter("optimizationType");

        RouteController.SearchResult result = routeController.search(source, destination, optimizationType);

        try (PrintWriter out = response.getWriter()) {
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }

            out.print(JsonMappers.routeResult(result.routeResult, optimizationType).toJson());
        }
    }
}
