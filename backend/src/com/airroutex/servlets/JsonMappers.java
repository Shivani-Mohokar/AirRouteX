package com.airroutex.servlets;

import com.airroutex.models.Airline;
import com.airroutex.models.Airport;
import com.airroutex.models.Booking;
import com.airroutex.models.BookingLeg;
import com.airroutex.models.Flight;
import com.airroutex.models.RouteResult;
import com.airroutex.utils.JsonArray;
import com.airroutex.utils.JsonObject;

/**
 * JsonMappers.java
 * ------------------------------------------------------------------
 * Converts model objects (Airport, Flight, RouteResult, Booking...)
 * into JsonObject/JsonArray for HTTP responses. Deliberately lives in
 * the servlets/ (presentation layer) package, not models/ - the
 * models themselves stay plain, framework-agnostic POJOs, and only
 * the presentation layer knows how to format them as JSON.
 * ------------------------------------------------------------------
 */
final class JsonMappers {

    private JsonMappers() {
    }

    static JsonObject airport(Airport a) {
        return new JsonObject()
                .put("airportId", a.getAirportId())
                .put("code", a.getCode())
                .put("city", a.getCity())
                .put("name", a.getName());
    }

    static JsonObject airline(Airline a) {
        return new JsonObject()
                .put("airlineId", a.getAirlineId())
                .put("code", a.getCode())
                .put("name", a.getName());
    }

    static JsonObject flight(Flight f) {
        return new JsonObject()
                .put("flightId", f.getFlightId())
                .put("flightNumber", f.getFlightNumber())
                .put("airlineId", f.getAirlineId())
                .put("airlineName", f.getAirlineName())
                .put("sourceAirportId", f.getSourceAirportId())
                .put("sourceCode", f.getSourceCode())
                .put("destinationAirportId", f.getDestinationAirportId())
                .put("destinationCode", f.getDestinationCode())
                .put("distanceKm", f.getDistanceKm())
                .put("price", f.getPrice())
                .put("durationMinutes", f.getDurationMinutes())
                .put("departureTime", String.valueOf(f.getDepartureTime()))
                .put("arrivalTime", String.valueOf(f.getArrivalTime()));
    }

    static JsonObject routeResult(RouteResult r, String optimizationType) {
        JsonArray flightsJson = new JsonArray();
        for (Flight f : r.getFlights()) {
            flightsJson.add(flight(f));
        }

        return new JsonObject()
                .put("optimizationType", optimizationType)
                .put("totalDistanceKm", r.getTotalDistanceKm())
                .put("totalPrice", r.getTotalPrice())
                .put("totalDurationMinutes", r.getTotalDurationMinutes())
                .put("numberOfLayovers", r.getNumberOfLayovers())
                .put("flights", flightsJson);
    }

    static JsonObject booking(Booking b) {
        JsonArray legsJson = new JsonArray();
        for (BookingLeg leg : b.getLegs()) {
            legsJson.add(flight(leg.getFlight()));
        }

        return new JsonObject()
                .put("bookingId", b.getBookingId())
                .put("optimizationType", b.getOptimizationType().name())
                .put("totalPrice", b.getTotalPrice())
                .put("totalDistanceKm", b.getTotalDistanceKm())
                .put("totalDurationMinutes", b.getTotalDurationMinutes())
                .put("numberOfLayovers", b.getNumberOfLayovers())
                .put("bookingDate", String.valueOf(b.getBookingDate()))
                .put("status", b.getStatus().name())
                .put("flights", legsJson);
    }
}
