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
 * LogoutServlet.java
 * ------------------------------------------------------------------
 * POST /AirRouteX/logout
 * Invalidates the HttpSession, ending the login.
 * ------------------------------------------------------------------
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        SessionUtil.invalidate(request);

        try (PrintWriter out = response.getWriter()) {
            out.print(new JsonObject().put("message", "Logged out successfully").toJson());
        }
    }
}
