package com.airroutex.servlets;

import com.airroutex.controllers.AdminController;
import com.airroutex.models.Airport;
import com.airroutex.utils.JsonArray;
import com.airroutex.utils.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminAirportServlet.java
 * ------------------------------------------------------------------
 * GET    /AirRouteX/admin/airports          -> list all airports
 * POST   /AirRouteX/admin/airports          -> add an airport (params: code, city, name)
 * DELETE /AirRouteX/admin/airports?id=1     -> delete an airport
 *
 * All methods require an ADMIN session (enforced by AdminServletBase).
 * ------------------------------------------------------------------
 */
@WebServlet("/admin/airports")
public class AdminAirportServlet extends AdminServletBase {

    private final AdminController adminController = new AdminController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            List<Airport> airports = adminController.listAirports();
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        String code = request.getParameter("code");
        String city = request.getParameter("city");
        String name = request.getParameter("name");

        AdminController.Result result = adminController.addAirport(code, city, name);

        try (PrintWriter out = response.getWriter()) {
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }
            out.print(new JsonObject().put("airportId", (Integer) result.data).toJson());
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

            AdminController.Result result = adminController.deleteAirport(Integer.parseInt(idParam));
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }
            out.print(new JsonObject().put("message", "Airport deleted").toJson());
        }
    }
}
