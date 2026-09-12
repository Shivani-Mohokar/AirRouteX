package com.airroutex.dao;

import com.airroutex.database.DBConnection;
import com.airroutex.models.Booking;
import com.airroutex.models.BookingLeg;
import com.airroutex.models.Flight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * BookingDAO.java
 * ------------------------------------------------------------------
 * DAO layer for Booking + BookingLeg. createBooking() writes BOTH
 * tables inside a single JDBC transaction (autoCommit=false, commit
 * on success, rollback on any failure) so a booking can never end up
 * "half saved" - either all legs are recorded, or none are.
 * ------------------------------------------------------------------
 */
public class BookingDAO {

    /**
     * Inserts the Booking header row plus one BookingLeg row per
     * flight in booking.getLegs(), all inside one transaction.
     * Returns the generated booking_id.
     */
    public int createBooking(Booking booking) throws SQLException {
        String insertBookingSql = "INSERT INTO Booking "
                + "(user_id, optimization_type, total_price, total_distance_km, "
                + " total_duration_minutes, number_of_layovers, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMED')";

        String insertLegSql = "INSERT INTO BookingLeg (booking_id, flight_id, leg_order) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int bookingId;
            try (PreparedStatement stmt = conn.prepareStatement(insertBookingSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, booking.getUserId());
                stmt.setString(2, booking.getOptimizationType().name());
                stmt.setDouble(3, booking.getTotalPrice());
                stmt.setDouble(4, booking.getTotalDistanceKm());
                stmt.setInt(5, booking.getTotalDurationMinutes());
                stmt.setInt(6, booking.getNumberOfLayovers());
                stmt.executeUpdate();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to obtain generated booking_id");
                    }
                    bookingId = keys.getInt(1);
                }
            }

            try (PreparedStatement legStmt = conn.prepareStatement(insertLegSql)) {
                int legOrder = 1;
                for (BookingLeg leg : booking.getLegs()) {
                    legStmt.setInt(1, bookingId);
                    legStmt.setInt(2, leg.getFlightId());
                    legStmt.setInt(3, legOrder++);
                    legStmt.addBatch();
                }
                legStmt.executeBatch();
            }

            conn.commit();
            return bookingId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /** Returns all bookings for a user, most recent first, each with its legs (and joined Flight details) populated. */
    public List<Booking> getBookingsByUser(int userId) throws SQLException {
        String bookingSql = "SELECT booking_id, user_id, optimization_type, total_price, total_distance_km, "
                + "total_duration_minutes, number_of_layovers, booking_date, status "
                + "FROM Booking WHERE user_id = ? ORDER BY booking_date DESC";

        List<Booking> bookings = new ArrayList<>();
        Map<Integer, Booking> bookingsById = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(bookingSql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapBookingRow(rs);
                    bookings.add(booking);
                    bookingsById.put(booking.getBookingId(), booking);
                }
            }
        }

        if (!bookingsById.isEmpty()) {
            attachLegs(bookingsById);
        }

        return bookings;
    }

    private void attachLegs(Map<Integer, Booking> bookingsById) throws SQLException {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < bookingsById.size(); i++) {
            placeholders.append(i == 0 ? "?" : ",?");
        }

        String legSql = "SELECT bl.booking_leg_id, bl.booking_id, bl.flight_id, bl.leg_order, "
                + "       f.flight_number, f.airline_id, al.name AS airline_name, "
                + "       f.source_airport_id, src.code AS source_code, "
                + "       f.destination_airport_id, dst.code AS destination_code, "
                + "       f.distance_km, f.price, f.duration_minutes, f.departure_time, f.arrival_time "
                + "FROM BookingLeg bl "
                + "JOIN Flight f ON bl.flight_id = f.flight_id "
                + "JOIN Airline al ON f.airline_id = al.airline_id "
                + "JOIN Airport src ON f.source_airport_id = src.airport_id "
                + "JOIN Airport dst ON f.destination_airport_id = dst.airport_id "
                + "WHERE bl.booking_id IN (" + placeholders + ") "
                + "ORDER BY bl.booking_id, bl.leg_order";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(legSql)) {

            int i = 1;
            for (Integer bookingId : bookingsById.keySet()) {
                stmt.setInt(i++, bookingId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookingLeg leg = new BookingLeg();
                    leg.setBookingLegId(rs.getInt("booking_leg_id"));
                    leg.setBookingId(rs.getInt("booking_id"));
                    leg.setFlightId(rs.getInt("flight_id"));
                    leg.setLegOrder(rs.getInt("leg_order"));

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
                    leg.setFlight(flight);

                    bookingsById.get(leg.getBookingId()).getLegs().add(leg);
                }
            }
        }
    }

    public void cancelBooking(int bookingId, int userId) throws SQLException {
        String sql = "UPDATE Booking SET status = 'CANCELLED' WHERE booking_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    private Booking mapBookingRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setOptimizationType(Booking.OptimizationType.valueOf(rs.getString("optimization_type")));
        booking.setTotalPrice(rs.getDouble("total_price"));
        booking.setTotalDistanceKm(rs.getDouble("total_distance_km"));
        booking.setTotalDurationMinutes(rs.getInt("total_duration_minutes"));
        booking.setNumberOfLayovers(rs.getInt("number_of_layovers"));
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setStatus(Booking.Status.valueOf(rs.getString("status")));
        return booking;
    }
}
