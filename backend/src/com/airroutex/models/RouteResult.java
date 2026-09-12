package com.airroutex.models;

import java.util.List;

/**
 * RouteResult.java
 * ------------------------------------------------------------------
 * Represents the OUTPUT of running BFSPathFinder or DijkstraPathFinder
 * on the in-memory Graph: the ordered list of Flight edges that make
 * up the optimal route, plus the aggregated totals. This class lives
 * in models/ (not algorithms/) because it is also used by the
 * controller/service layer to build a Booking - it's a shared data
 * shape, not algorithm logic itself.
 * ------------------------------------------------------------------
 */
public class RouteResult {

    private boolean found;
    private List<Flight> flights;   // ordered list of flight "legs" for this route
    private double totalDistanceKm;
    private double totalPrice;
    private int totalDurationMinutes;
    private int numberOfLayovers;

    public RouteResult() {
    }

    public static RouteResult notFound() {
        RouteResult r = new RouteResult();
        r.found = false;
        return r;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(int totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public int getNumberOfLayovers() {
        return numberOfLayovers;
    }

    public void setNumberOfLayovers(int numberOfLayovers) {
        this.numberOfLayovers = numberOfLayovers;
    }
}
