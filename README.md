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
