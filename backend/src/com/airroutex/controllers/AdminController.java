package com.airroutex.controllers;

import com.airroutex.dao.AirlineDAO;
import com.airroutex.dao.AirportDAO;
import com.airroutex.dao.FlightDAO;
import com.airroutex.models.Airline;
import com.airroutex.models.Airport;
import com.airroutex.models.Flight;

import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

/**
 * AdminController.java
 * ------------------------------------------------------------------
 * Controller/Service layer for admin-only operations: managing
 * airports, airlines, and flights. Servlets call these methods AFTER
 * confirming (via HttpSession) that the logged-in user has role
 * ADMIN - this class does not re-check the role itself, since that
 * is a cross-cutting concern handled once in the servlet layer
 * (see servlets.AdminFlightServlet for the session check pattern).
 * ------------------------------------------------------------------
 */
public class AdminController {

    private final AirportDAO airportDAO = new AirportDAO();
    private final AirlineDAO airlineDAO = new AirlineDAO();
    private final FlightDAO flightDAO = new FlightDAO();

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

    // ---------------- Airports ----------------

    public List<Airport> listAirports() throws SQLException {
        return airportDAO.getAllAirports();
    }

    public Result addAirport(String code, String city, String name) {
        try {
            if (isBlank(code) || isBlank(city) || isBlank(name)) {
                return Result.fail("code, city, and name are all required");
            }
            if (airportDAO.getAirportByCode(code.toUpperCase()) != null) {
                return Result.fail("An airport with code " + code.toUpperCase() + " already exists");
            }
            Airport airport = new Airport(0, code.toUpperCase(), city, name);
            int id = airportDAO.addAirport(airport);
            return Result.ok(id);
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    public Result deleteAirport(int airportId) {
        try {
            airportDAO.deleteAirport(airportId);
            return Result.ok(null);
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    // ---------------- Airlines ----------------

    public List<Airline> listAirlines() throws SQLException {
        return airlineDAO.getAllAirlines();
    }

    public Result addAirline(String code, String name) {
        try {
            if (isBlank(code) || isBlank(name)) {
                return Result.fail("code and name are both required");
            }
            Airline airline = new Airline(0, code.toUpperCase(), name);
            int id = airlineDAO.addAirline(airline);
            return Result.ok(id);
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    // ---------------- Flights ----------------

    public List<Flight> listFlights() throws SQLException {
        return flightDAO.getAllFlights();
    }

    public Result addFlight(String flightNumber, int airlineId, int sourceAirportId, int destinationAirportId,
                             double distanceKm, double price, int durationMinutes,
                             String departureTime, String arrivalTime) {
        Result validation = validateFlightInput(flightNumber, sourceAirportId, destinationAirportId,
                distanceKm, price, durationMinutes);
        if (validation != null) {
            return validation;
        }

        try {
            Flight flight = buildFlight(flightNumber, airlineId, sourceAirportId, destinationAirportId,
                    distanceKm, price, durationMinutes, departureTime, arrivalTime);
            int id = flightDAO.addFlight(flight);
            return Result.ok(id);
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    public Result updateFlight(int flightId, String flightNumber, int airlineId, int sourceAirportId,
                                int destinationAirportId, double distanceKm, double price, int durationMinutes,
                                String departureTime, String arrivalTime) {
        Result validation = validateFlightInput(flightNumber, sourceAirportId, destinationAirportId,
                distanceKm, price, durationMinutes);
        if (validation != null) {
            return validation;
        }

        try {
            Flight flight = buildFlight(flightNumber, airlineId, sourceAirportId, destinationAirportId,
                    distanceKm, price, durationMinutes, departureTime, arrivalTime);
            flightDAO.updateFlight(flightId, flight);
            return Result.ok(null);
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    public Result deleteFlight(int flightId) {
        try {
            flightDAO.deleteFlight(flightId);
            return Result.ok(null);
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    private Result validateFlightInput(String flightNumber, int sourceAirportId, int destinationAirportId,
                                        double distanceKm, double price, int durationMinutes) {
        if (isBlank(flightNumber)) {
            return Result.fail("flightNumber is required");
        }
        if (sourceAirportId == destinationAirportId) {
            return Result.fail("Source and destination airports must be different");
        }
        if (distanceKm <= 0 || price <= 0 || durationMinutes <= 0) {
            return Result.fail("distance, price, and duration must all be positive numbers");
        }
        return null; // no validation errors
    }

    private Flight buildFlight(String flightNumber, int airlineId, int sourceAirportId, int destinationAirportId,
                                double distanceKm, double price, int durationMinutes,
                                String departureTime, String arrivalTime) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setAirlineId(airlineId);
        flight.setSourceAirportId(sourceAirportId);
        flight.setDestinationAirportId(destinationAirportId);
        flight.setDistanceKm(distanceKm);
        flight.setPrice(price);
        flight.setDurationMinutes(durationMinutes);
        flight.setDepartureTime(Time.valueOf(departureTime.length() == 5 ? departureTime + ":00" : departureTime));
        flight.setArrivalTime(Time.valueOf(arrivalTime.length() == 5 ? arrivalTime + ":00" : arrivalTime));
        return flight;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
