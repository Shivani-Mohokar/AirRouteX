package com.airroutex.controllers;

import com.airroutex.dao.BookingDAO;
import com.airroutex.models.Booking;
import com.airroutex.models.BookingLeg;
import com.airroutex.models.Flight;
import com.airroutex.models.RouteResult;

import java.sql.SQLException;
import java.util.List;

/**
 * BookingController.java
 * ------------------------------------------------------------------
 * Controller/Service layer for bookings. Turns a RouteResult (the
 * output of a graph search) into a persisted Booking + BookingLeg
 * rows, and fetches booking history for a user.
 * ------------------------------------------------------------------
 */
public class BookingController {

    private final BookingDAO bookingDAO = new BookingDAO();

    public static class Result {
        public final boolean success;
        public final String errorMessage;
        public final Object data;

        private Result(boolean success, String errorMessage, Object data) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.data = data;
        }

        static Result ok(Object data) {
            return new Result(true, null, data);
        }

        static Result fail(String message) {
            return new Result(false, message, null);
        }
    }

    /**
     * Builds and persists a Booking from a previously computed RouteResult.
     * The RouteResult itself is NOT re-validated here (RouteController
     * already confirmed it's a real, reachable route) - this method's
     * job is purely persistence.
     */
    public Result createBooking(int userId, Booking.OptimizationType optimizationType, RouteResult routeResult) {
        try {
            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setOptimizationType(optimizationType);
            booking.setTotalPrice(routeResult.getTotalPrice());
            booking.setTotalDistanceKm(routeResult.getTotalDistanceKm());
            booking.setTotalDurationMinutes(routeResult.getTotalDurationMinutes());
            booking.setNumberOfLayovers(routeResult.getNumberOfLayovers());

            for (Flight flight : routeResult.getFlights()) {
                BookingLeg leg = new BookingLeg();
                leg.setFlightId(flight.getFlightId());
                booking.getLegs().add(leg);
            }

            int bookingId = bookingDAO.createBooking(booking);
            return Result.ok(bookingId);

        } catch (SQLException e) {
            return Result.fail("Database error while creating booking: " + e.getMessage());
        }
    }

    public Result getBookingHistory(int userId) {
        try {
            List<Booking> bookings = bookingDAO.getBookingsByUser(userId);
            return Result.ok(bookings);
        } catch (SQLException e) {
            return Result.fail("Database error while fetching booking history: " + e.getMessage());
        }
    }

    public Result cancelBooking(int bookingId, int userId) {
        try {
            bookingDAO.cancelBooking(bookingId, userId);
            return Result.ok(null);
        } catch (SQLException e) {
            return Result.fail("Database error while cancelling booking: " + e.getMessage());
        }
    }
}
