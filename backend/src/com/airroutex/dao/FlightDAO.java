package com.airroutex.dao;

import com.airroutex.database.DBConnection;
import com.airroutex.models.Flight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * FlightDAO.java
 * ------------------------------------------------------------------
 * DAO layer for the Flight table. getAllFlights() is the single most
 * important query in this project: its result is what
 * algorithms.GraphBuilder turns into the in-memory Graph before every
 * search, so the graph is ALWAYS built fresh from whatever is
 * currently in MySQL (admin edits take effect on the next search,
 * with no caching or restart needed).
 * ------------------------------------------------------------------
 */
public class FlightDAO {

    private static final String SELECT_WITH_JOINS =
            "SELECT f.flight_id, f.flight_number, f.airline_id, al.name AS airline_name, "
          + "       f.source_airport_id, src.code AS source_code, "
          + "       f.destination_airport_id, dst.code AS destination_code, "
          + "       f.distance_km, f.price, f.duration_minutes, "
          + "       f.departure_time, f.arrival_time "
          + "FROM Flight f "
          + "JOIN Airline al ON f.airline_id = al.airline_id "
          + "JOIN Airport src ON f.source_airport_id = src.airport_id "
          + "JOIN Airport dst ON f.destination_airport_id = dst.airport_id ";

    /** Reads every flight in the system. This is what feeds GraphBuilder. */
    public List<Flight> getAllFlights() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY f.flight_id";
        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                flights.add(mapRow(rs));
            }
        }
        return flights;
    }

    public Flight getFlightById(int flightId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE f.flight_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public int addFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO Flight "
                + "(flight_number, airline_id, source_airport_id, destination_airport_id, "
                + " distance_km, price, duration_minutes, departure_time, arrival_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            bindFlightParams(stmt, flight);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public void updateFlight(int flightId, Flight flight) throws SQLException {
        String sql = "UPDATE Flight SET flight_number = ?, airline_id = ?, source_airport_id = ?, "
                + "destination_airport_id = ?, distance_km = ?, price = ?, duration_minutes = ?, "
                + "departure_time = ?, arrival_time = ? WHERE flight_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindFlightParams(stmt, flight);
            stmt.setInt(10, flightId);
            stmt.executeUpdate();
        }
    }

    public void deleteFlight(int flightId) throws SQLException {
        String sql = "DELETE FROM Flight WHERE flight_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            stmt.executeUpdate();
        }
    }

    private void bindFlightParams(PreparedStatement stmt, Flight flight) throws SQLException {
        stmt.setString(1, flight.getFlightNumber());
        stmt.setInt(2, flight.getAirlineId());
        stmt.setInt(3, flight.getSourceAirportId());
        stmt.setInt(4, flight.getDestinationAirportId());
        stmt.setDouble(5, flight.getDistanceKm());
        stmt.setDouble(6, flight.getPrice());
        stmt.setInt(7, flight.getDurationMinutes());
        stmt.setTime(8, flight.getDepartureTime());
        stmt.setTime(9, flight.getArrivalTime());
    }

    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setAirlineId(rs.getInt("airline_id"));
        flight.setAirlineName(rs.getString("airline_name"));
        flight.setSourceAirportId(rs.getInt("source_airport_id"));
        flight.setSourceCode(rs.getString("source_code"));
        flight.setDestinationAirportId(rs.getInt("destination_airport_id"));
        flight.setDestinationCode(rs.getString("destination_code"));
        flight.setDistanceKm(rs.getDouble("distance_km"));
        flight.setPrice(rs.getDouble("price"));
        flight.setDurationMinutes(rs.getInt("duration_minutes"));
        flight.setDepartureTime(rs.getTime("departure_time"));
        flight.setArrivalTime(rs.getTime("arrival_time"));
        return flight;
    }
}
