package com.airroutex.dao;

import com.airroutex.database.DBConnection;
import com.airroutex.models.Airline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * AirlineDAO.java
 * ------------------------------------------------------------------
 * DAO layer for the Airline table.
 * ------------------------------------------------------------------
 */
public class AirlineDAO {

    public List<Airline> getAllAirlines() throws SQLException {
        String sql = "SELECT airline_id, code, name FROM Airline ORDER BY name";
        List<Airline> airlines = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                airlines.add(mapRow(rs));
            }
        }
        return airlines;
    }

    public Airline getAirlineById(int airlineId) throws SQLException {
        String sql = "SELECT airline_id, code, name FROM Airline WHERE airline_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, airlineId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public int addAirline(Airline airline) throws SQLException {
        String sql = "INSERT INTO Airline (code, name) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, airline.getCode());
            stmt.setString(2, airline.getName());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    private Airline mapRow(ResultSet rs) throws SQLException {
        return new Airline(
                rs.getInt("airline_id"),
                rs.getString("code"),
                rs.getString("name")
        );
    }
}
