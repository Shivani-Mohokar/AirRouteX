# ✈️ AirRouteX

<p align="center">
  <b>Graph-Based Airline Route Optimization System</b>
</p>

<p align="center">
  A Java web application that uses graph algorithms to find optimal flight routes based on distance, price, duration, and layovers.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Java%20Servlets-6A1B9A?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/JDBC-2E7D32?style=for-the-badge">
  <img src="https://img.shields.io/badge/Graph%20Algorithms-7B1FA2?style=for-the-badge">
  <img src="https://img.shields.io/badge/Tomcat-F8A100?style=for-the-badge&logo=apachetomcat&logoColor=black">
</p>

---

## ✨ Features

- 🔍 Shortest-distance route
- 💰 Lowest-price route
- ⏱️ Shortest-duration route
- 🛫 Minimum-layover route
- 👤 User registration & login
- 🎫 Flight booking
- 🛠️ Admin flight management
- 🗄️ MySQL/MariaDB database integration

---

## 🧠 Algorithms

| Requirement | Algorithm |
|---|---|
| Shortest Distance | Dijkstra's Algorithm |
| Lowest Price | Dijkstra's Algorithm |
| Shortest Duration | Dijkstra's Algorithm |
| Minimum Layovers | BFS |

The flight network is represented using an **adjacency list**:

```text
HashMap<Integer, List<Flight>>
🛠️ Tech Stack
<p> <img src="https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/Servlets-6A1B9A?style=for-the-badge"> <img src="https://img.shields.io/badge/JDBC-2E7D32?style=for-the-badge"> <img src="https://img.shields.io/badge/MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black"> <img src="https://img.shields.io/badge/BCrypt-8E24AA?style=for-the-badge"> <img src="https://img.shields.io/badge/Tomcat-F8A100?style=for-the-badge&logo=apachetomcat&logoColor=black"> </p>

Backend: Java 17, Java Servlets, JDBC
Frontend: HTML, CSS, JavaScript
Database: MySQL / MariaDB
DSA: Graph, Dijkstra, BFS, HashMap, PriorityQueue
Security: HttpSession, BCrypt

🏗️ Architecture
<img src="docs/Architecture-Diagram.png" alt="AirRouteX Architecture" width="850"/>
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
🗃️ Database Design
<img src="docs/ER-Diagram.png" alt="AirRouteX ER Diagram" width="850"/>
⚡ Complexity
Operation	Time Complexity
Graph Construction	O(V + E)
BFS	O(V + E)
Dijkstra (Priority Queue)	O((V + E) log V)

Where:

V = number of airports
E = number of flights
🔐 Security
Passwords are stored using BCrypt hashing
User sessions are managed using HttpSession
Database credentials are kept outside the repository
📂 Project Structure
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
│   ├── schema.sql
│   └── sample_data.sql
├── docs/
│   ├── Architecture-Diagram.png
│   ├── ER-Diagram.png
│   └── Complexity-Analysis.md
├── build.sh
├── .gitignore
└── README.md
⚙️ Setup
Requirements
Java JDK 17+
Apache Tomcat 9
MySQL / MariaDB
Database

Run:

database/schema.sql

Then:

database/sample_data.sql

Create:

backend/db.properties

using backend/db.properties.example and add your database credentials.

db.properties is ignored by Git and should not be committed.

📚 Documentation

Additional documentation is available in the docs/ directory:

Architecture Diagram
ER Diagram
Complexity Analysis
👩‍💻 Author

Shivani Mohokar

GitHub

⭐ If you find AirRouteX useful, consider giving the repository a star!
