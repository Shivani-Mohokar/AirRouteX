package com.airroutex.dao;

import com.airroutex.database.DBConnection;
import com.airroutex.models.Airport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AirportDAO.java
 * ------------------------------------------------------------------
 * DAO (Data Access Object) layer for the Airport table. Contains raw
 * SQL and JDBC calls ONLY - no business logic and no graph/algorithm
 * code. Every method opens its own Connection via try-with-resources
 * so connections are never leaked even if an exception is thrown.
 * ------------------------------------------------------------------
 */
public class AirportDAO {

    public List<Airport> getAllAirports() throws SQLException {
        String sql = "SELECT airport_id, code, city, name FROM Airport ORDER BY city";
        List<Airport> airports = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                airports.add(mapRow(rs));
            }
        }
        return airports;
    }

    public Airport getAirportById(int airportId) throws SQLException {
        String sql = "SELECT airport_id, code, city, name FROM Airport WHERE airport_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, airportId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Airport getAirportByCode(String code) throws SQLException {
        String sql = "SELECT airport_id, code, city, name FROM Airport WHERE code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public int addAirport(Airport airport) throws SQLException {
        String sql = "INSERT INTO Airport (code, city, name) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, airport.getCode());
            stmt.setString(2, airport.getCity());
            stmt.setString(3, airport.getName());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void deleteAirport(int airportId) throws SQLException {
        String sql = "DELETE FROM Airport WHERE airport_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, airportId);
            stmt.executeUpdate();
        }
    }

    private Airport mapRow(ResultSet rs) throws SQLException {
        return new Airport(
                rs.getInt("airport_id"),
                rs.getString("code"),
                rs.getString("city"),
                rs.getString("name")
        );
    }
}
