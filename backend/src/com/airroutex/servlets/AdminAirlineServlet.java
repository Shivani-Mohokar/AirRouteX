package com.airroutex.servlets;

import com.airroutex.controllers.AdminController;
import com.airroutex.models.Airline;
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
 * AdminAirlineServlet.java
 * ------------------------------------------------------------------
 * GET  /AirRouteX/admin/airlines  -> list all airlines
 * POST /AirRouteX/admin/airlines  -> add an airline (params: code, name)
 * ------------------------------------------------------------------
 */
@WebServlet("/admin/airlines")
public class AdminAirlineServlet extends AdminServletBase {

    private final AdminController adminController = new AdminController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAdmin(request, response)) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            List<Airline> airlines = adminController.listAirlines();
            JsonArray json = new JsonArray();
            for (Airline a : airlines) {
                json.add(JsonMappers.airline(a));
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
        String name = request.getParameter("name");

        AdminController.Result result = adminController.addAirline(code, name);

        try (PrintWriter out = response.getWriter()) {
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }
            out.print(new JsonObject().put("airlineId", (Integer) result.data).toJson());
        }
    }
}
