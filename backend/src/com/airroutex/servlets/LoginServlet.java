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
 * LoginServlet.java
 * ------------------------------------------------------------------
 * POST /AirRouteX/login
 *   params: username, password
 *
 * On success, stores the User in HttpSession (via SessionUtil) so
 * subsequent requests from the same browser are recognized as
 * logged in - this IS the authentication mechanism for this project
 * (plain Java sessions, no JWT, as required).
 * ------------------------------------------------------------------
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthController authController = new AuthController();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        AuthController.AuthResult result = authController.login(username, password);

        try (PrintWriter out = response.getWriter()) {
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }

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
