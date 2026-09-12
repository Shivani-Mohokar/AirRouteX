package com.airroutex.dao;

import com.airroutex.database.DBConnection;
import com.airroutex.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDAO.java
 * ------------------------------------------------------------------
 * DAO layer for the `User` table (backtick-quoted in every query
 * because USER is a reserved word in MySQL). Only ever stores/reads
 * password HASHES - plain-text passwords never reach this class
 * (hashing happens in controllers.AuthController via utils.PasswordUtil
 * before addUser() is called).
 * ------------------------------------------------------------------
 */
public class UserDAO {

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email, role FROM `User` WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email, role FROM `User` WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        return getUserByUsername(username) != null;
    }

    /** Inserts a new user. passwordHash MUST already be a BCrypt hash, never plain text. */
    public int addUser(String username, String passwordHash, String email, User.Role role) throws SQLException {
        String sql = "INSERT INTO `User` (username, password_hash, email, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setString(3, email);
            stmt.setString(4, role.name());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        return user;
    }
}
