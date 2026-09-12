package com.airroutex.models;

import java.io.Serializable;

/**
 * User.java
 * ------------------------------------------------------------------
 * Plain POJO mapping to the `User` table. Note passwordHash is only
 * ever populated when reading from the DB for login verification -
 * it is NEVER sent to the frontend (see controllers.AuthController,
 * which builds a session/response object without this field).
 *
 * Implements Serializable because a User object is stored directly
 * in HttpSession (see utils.SessionUtil) - Tomcat may serialize
 * session data (e.g. during a restart with session persistence
 * enabled), so anything placed in a session should be Serializable.
 * ------------------------------------------------------------------
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Role {
        USER,
        ADMIN
    }

    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private Role role;

    public User() {
    }

    public User(int userId, String username, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
