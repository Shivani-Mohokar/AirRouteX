package com.airroutex.controllers;

import com.airroutex.dao.UserDAO;
import com.airroutex.models.User;
import com.airroutex.utils.PasswordUtil;

import java.sql.SQLException;

/**
 * AuthController.java
 * ------------------------------------------------------------------
 * Controller/Service layer for authentication. Servlets (Presentation
 * Layer) call these methods instead of touching UserDAO or
 * PasswordUtil directly - this is where the actual business rules
 * live (e.g. "passwords must be at least 6 characters", "usernames
 * must be unique", "new signups are always role USER").
 * ------------------------------------------------------------------
 */
public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    public static class AuthResult {
        public final boolean success;
        public final String errorMessage;
        public final User user;

        private AuthResult(boolean success, String errorMessage, User user) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.user = user;
        }

        static AuthResult ok(User user) {
            return new AuthResult(true, null, user);
        }

        static AuthResult fail(String message) {
            return new AuthResult(false, message, null);
        }
    }

    /**
     * Registers a new user. Always creates role = USER - there is no
     * way for the public registration form to create an ADMIN account
     * (admins are seeded directly in the database - see sample_data.sql).
     */
    public AuthResult register(String username, String password, String email) {
        try {
            if (username == null || username.trim().length() < 3) {
                return AuthResult.fail("Username must be at least 3 characters");
            }
            if (password == null || password.length() < 6) {
                return AuthResult.fail("Password must be at least 6 characters");
            }
            if (userDAO.usernameExists(username)) {
                return AuthResult.fail("That username is already taken");
            }

            String passwordHash = PasswordUtil.hash(password);
            int userId = userDAO.addUser(username, passwordHash, email, User.Role.USER);

            User user = new User(userId, username, email, User.Role.USER);
            return AuthResult.ok(user);

        } catch (SQLException e) {
            return AuthResult.fail("A database error occurred while registering: " + e.getMessage());
        }
    }

    /** Verifies credentials and returns the matching User (without the password hash) if valid. */
    public AuthResult login(String username, String password) {
        try {
            if (username == null || password == null) {
                return AuthResult.fail("Username and password are required");
            }

            User storedUser = userDAO.getUserByUsername(username);
            if (storedUser == null) {
                return AuthResult.fail("Invalid username or password");
            }

            boolean passwordMatches = PasswordUtil.verify(password, storedUser.getPasswordHash());
            if (!passwordMatches) {
                return AuthResult.fail("Invalid username or password");
            }

            User safeUser = new User(storedUser.getUserId(), storedUser.getUsername(),
                    storedUser.getEmail(), storedUser.getRole());
            return AuthResult.ok(safeUser);

        } catch (SQLException e) {
            return AuthResult.fail("A database error occurred while logging in: " + e.getMessage());
        }
    }
}
