package com.airroutex.utils;

import com.airroutex.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * SessionUtil.java
 * ------------------------------------------------------------------
 * Small helper around HttpSession so every servlet stores/reads the
 * logged-in user the exact same way. Authentication uses plain
 * Java HttpSession (no JWT, as required) - the session cookie
 * (JSESSIONID) issued by Tomcat is what keeps the user logged in
 * between requests.
 * ------------------------------------------------------------------
 */
public class SessionUtil {

    private static final String SESSION_USER_KEY = "loggedInUser";

    private SessionUtil() {
        // utility class - no instances
    }

    public static void setLoggedInUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER_KEY, user);
    }

    /** Returns the logged-in user, or null if nobody is logged in (no session, or no attribute set). */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false = don't create a new one just to check
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null && user.isAdmin();
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
