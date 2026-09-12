package com.airroutex.models;

import java.sql.Time;

/**
 * Flight.java
 * ------------------------------------------------------------------
 * Plain POJO mapping to the Flight table. In graph terms, every
 * Flight is a directed, weighted EDGE from sourceAirportId to
 * destinationAirportId.
 *
 * A Flight carries THREE independent weights - distanceKm, price,
 * and durationMinutes - because the same Dijkstra implementation
 * is reused for all three optimization types; only the weight
 * that gets read changes (see algorithms.WeightType).
 *
 * Fields like sourceCode/destinationCode/airlineName are convenience
 * "joined" fields populated by FlightDAO for display purposes; they
 * are not stored directly in the Flight table itself.
 * ------------------------------------------------------------------
 */
public class Flight {

    private int flightId;
    private String flightNumber;

    private int airlineId;
    private String airlineName; // joined, for display

    private int sourceAirportId;
    private String sourceCode;  // joined, for display

    private int destinationAirportId;
    private String destinationCode; // joined, for display

    private double distanceKm;
    private double price;
    private int durationMinutes;

    private Time departureTime;
    private Time arrivalTime;

    public Flight() {
    }

    // ---- Getters and setters ----

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public int getSourceAirportId() {
        return sourceAirportId;
    }

    public void setSourceAirportId(int sourceAirportId) {
        this.sourceAirportId = sourceAirportId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int getDestinationAirportId() {
        return destinationAirportId;
    }

    public void setDestinationAirportId(int destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
