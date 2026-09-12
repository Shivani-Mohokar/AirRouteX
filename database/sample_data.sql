-- ============================================================
-- AirRouteX - Sample Data
-- A small network of Indian cities, deliberately varied so that
-- shortest-distance, cheapest, fastest, and fewest-layover
-- routes are NOT always the same route (good for demoing that
-- the optimization type actually changes the answer).
-- ============================================================

USE airroutex_db;

-- ---------------- Airports (graph vertices) ----------------
INSERT INTO Airport (code, city, name) VALUES
('DEL', 'Delhi',     'Indira Gandhi International Airport'),
('BOM', 'Mumbai',    'Chhatrapati Shivaji Maharaj Intl Airport'),
('BLR', 'Bengaluru', 'Kempegowda International Airport'),
('HYD', 'Hyderabad', 'Rajiv Gandhi International Airport'),
('MAA', 'Chennai',   'Chennai International Airport'),
('CCU', 'Kolkata',   'Netaji Subhas Chandra Bose Intl Airport'),
('PNQ', 'Pune',      'Pune Airport');

-- ---------------- Airlines ----------------
INSERT INTO Airline (code, name) VALUES
('AI', 'Air India'),
('6E', 'IndiGo'),
('SG', 'SpiceJet');

-- ---------------- Flights (graph edges) ----------------
-- airline_id: 1=Air India, 2=IndiGo, 3=SpiceJet
-- airport_id: 1=DEL,2=BOM,3=BLR,4=HYD,5=MAA,6=CCU,7=PNQ
INSERT INTO Flight (flight_number, airline_id, source_airport_id, destination_airport_id, distance_km, price, duration_minutes, departure_time, arrival_time) VALUES
-- DEL -> BOM (direct, short distance & fast, but pricier)
('AI101', 1, 1, 2, 1150, 6500, 130, '06:00:00', '08:10:00'),
-- DEL -> PNQ -> BOM (cheaper connection, longer combined distance/time)
('6E102', 2, 1, 7, 1180, 3200, 140, '07:00:00', '09:20:00'),
('6E103', 2, 7, 2, 150,  1800, 60,  '10:30:00', '11:30:00'),
-- DEL -> BLR (direct)
('AI104', 1, 1, 3, 1740, 5200, 165, '09:00:00', '11:45:00'),
-- DEL -> HYD -> BLR (cheaper, more stops)
('6E105', 2, 1, 4, 1250, 3000, 120, '06:30:00', '08:30:00'),
('SG106', 3, 4, 3, 500,  2200, 75,  '09:30:00', '10:45:00'),
-- BOM -> BLR (direct)
('AI107', 1, 2, 3, 840,  4200, 95,  '12:00:00', '13:35:00'),
-- BOM -> HYD -> BLR
('6E108', 2, 2, 4, 620,  2800, 80,  '13:00:00', '14:20:00'),
('SG109', 3, 4, 3, 500,  2200, 75,  '15:00:00', '16:15:00'),
-- BLR -> MAA (direct, short hop)
('6E110', 2, 3, 5, 290,  1800, 55,  '17:00:00', '17:55:00'),
-- HYD -> MAA (direct)
('AI111', 1, 4, 5, 630,  2600, 70,  '11:00:00', '12:10:00'),
-- DEL -> CCU (direct)
('AI112', 1, 1, 6, 1300, 4800, 145, '08:00:00', '10:25:00'),
-- CCU -> BLR
('6E113', 2, 6, 3, 1560, 5300, 150, '11:00:00', '13:30:00'),
-- BOM -> MAA (direct)
('AI114', 1, 2, 5, 1030, 5000, 105, '14:30:00', '16:15:00'),
-- DEL -> CCU -> HYD -> BLR (long, but cheap segments)
('SG115', 3, 1, 6, 1300, 2500, 200, '05:00:00', '08:20:00'),
('SG116', 3, 6, 4, 1180, 2400, 130, '09:00:00', '11:10:00'),
('SG117', 3, 4, 3, 500,  2200, 75,  '12:00:00', '13:15:00');

-- ---------------- Demo Accounts ----------------
-- Passwords are bcrypt hashes generated with jBCrypt (cost factor 10).
-- Plain-text passwords for testing:
--   admin / admin123   (role: ADMIN)
--   demo  / user123    (role: USER)
INSERT INTO `User` (username, password_hash, email, role) VALUES
('admin', '$2a$10$z3J.ptF9je0QD1PRdgbHp.ymFbgaF7thBiwWGokAmLiNYr5/UiUre', 'admin@airroutex.local', 'ADMIN'),
('demo',  '$2a$10$G5hVvS0PLCW4/8RTvnepEe/TE.wcPL8whDrKHtRj2jjDWPliRX4Qi', 'demo@airroutex.local',  'USER');
