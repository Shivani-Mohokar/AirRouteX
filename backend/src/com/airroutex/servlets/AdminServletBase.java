package com.airroutex.servlets;

import com.airroutex.utils.JsonObject;
import com.airroutex.utils.SessionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * AdminServletBase.java
 * ------------------------------------------------------------------
 * Shared base class for every admin-only servlet. requireAdmin()
 * centralizes the "is this user logged in AND an admin?" check so
 * it's implemented exactly once instead of copy-pasted into every
 * admin servlet - if the rule ever changes, only this file changes.
 *
 * Returns true if the request is allowed to proceed; if it returns
 * false, it has ALREADY written the 401/403 JSON error response, so
 * the calling servlet should just `return` immediately.
 * ------------------------------------------------------------------
 */
public abstract class AdminServletBase extends HttpServlet {

    protected boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!SessionUtil.isLoggedIn(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print(new JsonObject().put("error", "Please log in first").toJson());
            }
            return false;
        }

        if (!SessionUtil.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            try (PrintWriter out = response.getWriter()) {
                out.print(new JsonObject().put("error", "Admin access required").toJson());
            }
            return false;
        }

        return true;
    }
}
