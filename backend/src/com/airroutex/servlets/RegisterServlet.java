package com.airroutex.servlets;

import com.airroutex.controllers.AuthController;
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
 * RegisterServlet.java
 * ------------------------------------------------------------------
 * Presentation layer. Reads plain form parameters (NOT a JSON body -
 * this project avoids adding a JSON-parsing dependency, see
 * utils.JsonUtil), delegates all real logic to AuthController, and
 * writes back a small JSON response.
 *
 * POST /AirRouteX/register
 *   params: username, password, email (optional)
 * ------------------------------------------------------------------
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final AuthController authController = new AuthController();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        AuthController.AuthResult result = authController.register(username, password, email);

        try (PrintWriter out = response.getWriter()) {
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }

            // Auto-login after successful registration
            SessionUtil.setLoggedInUser(request, result.user);

            out.print(buildUserJson(result.user).toJson());
        }
    }

    private JsonObject buildUserJson(User user) {
        return new JsonObject()
                .put("userId", user.getUserId())
                .put("username", user.getUsername())
                .put("email", user.getEmail())
                .put("role", user.getRole().name());
    }
}
