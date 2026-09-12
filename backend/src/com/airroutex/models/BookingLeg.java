package com.airroutex.models;

/**
 * BookingLeg.java
 * ------------------------------------------------------------------
 * Plain POJO mapping to the BookingLeg table - one row per flight
 * within a Booking, in order. The `flight` field is populated by
 * BookingDAO with the full Flight details (joined) for display.
 * ------------------------------------------------------------------
 */
public class BookingLeg {

    private int bookingLegId;
    private int bookingId;
    private int flightId;
    private int legOrder;
    private Flight flight; // joined, for display

    public BookingLeg() {
    }

    public int getBookingLegId() {
        return bookingLegId;
    }

    public void setBookingLegId(int bookingLegId) {
        this.bookingLegId = bookingLegId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getLegOrder() {
        return legOrder;
    }

    public void setLegOrder(int legOrder) {
        this.legOrder = legOrder;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}
