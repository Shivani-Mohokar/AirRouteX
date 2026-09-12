-- ============================================================
-- AirRouteX - Database Schema
-- Normalized to 3NF.
--
-- Design notes:
--   - Airport and Airline are independent reference tables.
--   - Flight references both (many flights per airport, many
--     flights per airline) -> classic 1-to-many relationships,
--     no repeating groups, no transitive dependencies -> 3NF.
--   - A route the user searches for may involve MULTIPLE flights
--     (layovers). To store a booking of a multi-leg route without
--     violating 3NF (a Booking table with flight_id1, flight_id2,
--     flight_id3... columns would NOT be normalized - repeating
--     groups), we split Booking into:
--       Booking      -> one row per booking (who booked, when, totals)
--       BookingLeg   -> one row per flight WITHIN that booking,
--                       in order (leg_order = 1, 2, 3...)
--     This is a standard normalization pattern for "order with
--     multiple line items" style data.
-- ============================================================

CREATE DATABASE IF NOT EXISTS airroutex_db;
USE airroutex_db;

-- ------------------------------------------------------------
-- Airport: one row per airport. A VERTEX in the flight graph.
-- ------------------------------------------------------------
CREATE TABLE Airport (
    airport_id   INT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(10)  NOT NULL UNIQUE,   -- e.g. 'DEL'
    city         VARCHAR(100) NOT NULL,
    name         VARCHAR(150) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Airline: one row per airline operating flights.
-- ------------------------------------------------------------
CREATE TABLE Airline (
    airline_id   INT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(10)  NOT NULL UNIQUE,   -- e.g. 'AI'
    name         VARCHAR(150) NOT NULL
);

-- ------------------------------------------------------------
-- Flight: one row per scheduled flight. An EDGE in the flight
-- graph, directed from source_airport_id -> destination_airport_id.
-- Carries THREE independent edge weights: distance_km, price,
-- and duration_minutes, so the same Dijkstra implementation can
-- optimize for any of them depending on which column is read.
-- ------------------------------------------------------------
CREATE TABLE Flight (
    flight_id             INT AUTO_INCREMENT PRIMARY KEY,
    flight_number         VARCHAR(20) NOT NULL,
    airline_id            INT NOT NULL,
    source_airport_id     INT NOT NULL,
    destination_airport_id INT NOT NULL,
    distance_km           DECIMAL(8, 2) NOT NULL,
    price                 DECIMAL(10, 2) NOT NULL,
    duration_minutes      INT NOT NULL,
    departure_time        TIME NOT NULL,
    arrival_time           TIME NOT NULL,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_flight_airline
        FOREIGN KEY (airline_id) REFERENCES Airline(airline_id) ON DELETE CASCADE,
    CONSTRAINT fk_flight_source
        FOREIGN KEY (source_airport_id) REFERENCES Airport(airport_id) ON DELETE CASCADE,
    CONSTRAINT fk_flight_destination
        FOREIGN KEY (destination_airport_id) REFERENCES Airport(airport_id) ON DELETE CASCADE,
    CONSTRAINT chk_flight_source_dest_different
        CHECK (source_airport_id <> destination_airport_id)
);

-- ------------------------------------------------------------
-- `User`: registered users. Table name is backtick-quoted
-- everywhere it's used because USER is a reserved word in MySQL.
-- Passwords are stored as BCrypt hashes ONLY - never plain text.
-- ------------------------------------------------------------
CREATE TABLE `User` (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(150),
    role          ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Booking: one row per booking made by a user (the "header").
-- ------------------------------------------------------------
CREATE TABLE Booking (
    booking_id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id               INT NOT NULL,
    optimization_type     ENUM('DISTANCE', 'PRICE', 'DURATION', 'LAYOVERS') NOT NULL,
    total_price            DECIMAL(10, 2) NOT NULL,
    total_distance_km      DECIMAL(8, 2) NOT NULL,
    total_duration_minutes INT NOT NULL,
    number_of_layovers     INT NOT NULL,
    booking_date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status                  ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES `User`(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- BookingLeg: one row per flight WITHIN a booking (the "line
-- items"). leg_order preserves the sequence of a multi-hop route.
-- ------------------------------------------------------------
CREATE TABLE BookingLeg (
    booking_leg_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id     INT NOT NULL,
    flight_id      INT NOT NULL,
    leg_order      INT NOT NULL,   -- 1 = first flight, 2 = second flight, ...

    CONSTRAINT fk_bookingleg_booking
        FOREIGN KEY (booking_id) REFERENCES Booking(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_bookingleg_flight
        FOREIGN KEY (flight_id) REFERENCES Flight(flight_id) ON DELETE CASCADE,
    UNIQUE KEY uq_booking_leg_order (booking_id, leg_order)
);

-- ------------------------------------------------------------
-- Indexes to speed up graph construction (reading ALL flights)
-- and the most common lookups.
-- ------------------------------------------------------------
CREATE INDEX idx_flight_source ON Flight(source_airport_id);
CREATE INDEX idx_flight_destination ON Flight(destination_airport_id);
CREATE INDEX idx_bookingleg_booking ON BookingLeg(booking_id);
CREATE INDEX idx_booking_user ON Booking(user_id);
