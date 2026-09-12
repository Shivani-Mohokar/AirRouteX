# ✈️ AirRouteX

### Graph-Based Airline Route Optimization System

AirRouteX is a Java-based airline route optimization system that models flight networks as graphs and finds optimal routes based on distance, price, travel duration, and number of layovers.

---

## Features

- Shortest-distance route
- Lowest-price route
- Shortest-duration route
- Minimum-layover route
- User registration & login
- Flight booking
- Admin flight management
- MySQL/MariaDB database integration

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Java%20Servlets-6A1B9A?style=for-the-badge" />
  <img src="https://img.shields.io/badge/MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/JDBC-2E7D32?style=for-the-badge" />
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" />
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" />
  <img src="https://img.shields.io/badge/Apache%20Tomcat-F8A100?style=for-the-badge&logo=apachetomcat&logoColor=black" />
  <img src="https://img.shields.io/badge/BCrypt-8E24AA?style=for-the-badge" />
</p>

---

---

## Algorithms

| Requirement | Algorithm |
|---|---|
| Shortest Distance | Dijkstra's Algorithm |
| Lowest Price | Dijkstra's Algorithm |
| Shortest Duration | Dijkstra's Algorithm |
| Minimum Layovers | BFS |

The flight network is represented using an adjacency list:

```text
HashMap<Integer, List<Flight>>
```

## Architecture

<p align="center">
  <img src="docs/Architecture-Diagram.png" alt="AirRouteX Architecture" width="800"/>
</p>

```text
Frontend
   ↓
Java Servlets
   ↓
Route Optimization
(Dijkstra / BFS)
   ↓
DAO Layer + JDBC
   ↓
MySQL / MariaDB

---
```

## Database Design

<p align="center">
  <img src="docs/ER-Diagram.png" alt="AirRouteX ER Diagram" width="800"/>
</p>

The database stores information about users, airports, flights, and bookings.

---

## Complexity

| Operation | Time Complexity |
|---|---|
| Graph Construction | O(V + E) |
| BFS | O(V + E) |
| Dijkstra (Priority Queue) | O((V + E) log V) |

Where `V` is the number of airports and `E` is the number of flights.

---

## Security

- Passwords are protected using BCrypt hashing
- User sessions are managed using HttpSession
- Database credentials are kept outside the repository

  ---

## Project Structure

```text
AirRouteX/
├── frontend/
├── backend/
│   ├── src/com/airroutex/
│   │   ├── algorithms/
│   │   ├── controllers/
│   │   ├── dao/
│   │   ├── database/
│   │   ├── models/
│   │   ├── servlets/
│   │   └── utils/
│   ├── lib/
│   ├── web.xml
│   └── db.properties.example
├── database/
├── docs/
├── build.sh
├── .gitignore
└── README.md

---
```

## Setup

### Requirements

- Java JDK 17+
- Apache Tomcat 9
- MySQL / MariaDB

### Database

Run the SQL scripts from the `database/` folder:

```text
schema.sql
sample_data.sql
```

### Configuration

Copy `backend/db.properties.example` to:

```text
backend/db.properties
```
