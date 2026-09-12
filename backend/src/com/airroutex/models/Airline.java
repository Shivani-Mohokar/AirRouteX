package com.airroutex.models;

/**
 * Airline.java
 * ------------------------------------------------------------------
 * Plain POJO mapping to the Airline table. Not a graph vertex or
 * edge itself - just descriptive data attached to each Flight edge.
 * ------------------------------------------------------------------
 */
public class Airline {

    private int airlineId;
    private String code;
    private String name;

    public Airline() {
    }

    public Airline(int airlineId, String code, String name) {
        this.airlineId = airlineId;
        this.code = code;
        this.name = name;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
