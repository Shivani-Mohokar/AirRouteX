package com.airroutex.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking.java
 * ------------------------------------------------------------------
 * Plain POJO mapping to the Booking table (the "header" row for a
 * booked route). Holds a list of BookingLeg objects representing
 * each individual flight in the route, in order - this mirrors the
 * Booking/BookingLeg normalization explained in schema.sql.
 * ------------------------------------------------------------------
 */
public class Booking {

    public enum OptimizationType {
        DISTANCE, PRICE, DURATION, LAYOVERS
    }

    public enum Status {
        CONFIRMED, CANCELLED
    }

    private int bookingId;
    private int userId;
    private OptimizationType optimizationType;
    private double totalPrice;
    private double totalDistanceKm;
    private int totalDurationMinutes;
    private int numberOfLayovers;
    private Timestamp bookingDate;
    private Status status;
    private List<BookingLeg> legs = new ArrayList<>();

    public Booking() {
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OptimizationType getOptimizationType() {
        return optimizationType;
    }

    public void setOptimizationType(OptimizationType optimizationType) {
        this.optimizationType = optimizationType;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
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

    public Timestamp getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Timestamp bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<BookingLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<BookingLeg> legs) {
        this.legs = legs;
    }
}
