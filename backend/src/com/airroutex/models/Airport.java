package com.airroutex.models;

/**
 * Airport.java
 * ------------------------------------------------------------------
 * Plain POJO mapping to the Airport table. In graph terms, every
 * Airport is a VERTEX in the flight network.
 * ------------------------------------------------------------------
 */
public class Airport {

    private int airportId;
    private String code;
    private String city;
    private String name;

    public Airport() {
    }

    public Airport(int airportId, String code, String city, String name) {
        this.airportId = airportId;
        this.code = code;
        this.city = city;
        this.name = name;
    }

    public int getAirportId() {
        return airportId;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return code + " (" + city + ")";
    }
}
