package com.airroutex.servlets;

import com.airroutex.models.User;
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
 * CurrentUserServlet.java
 * ------------------------------------------------------------------
 * GET /AirRouteX/current-user
 *
 * The frontend calls this once on page load to find out whether the
 * browser already has a valid session (e.g. the user refreshed the
 * page) - this is how login state "survives" a refresh without any
 * client-side token storage.
 * ------------------------------------------------------------------
 */
@WebServlet("/current-user")
public class CurrentUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = SessionUtil.getLoggedInUser(request);

        try (PrintWriter out = response.getWriter()) {
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(new JsonObject().put("error", "Not logged in").toJson());
                return;
            }

            out.print(new JsonObject()
                    .put("userId", user.getUserId())
                    .put("username", user.getUsername())
                    .put("email", user.getEmail())
                    .put("role", user.getRole().name())
                    .toJson());
        }
    }
}
