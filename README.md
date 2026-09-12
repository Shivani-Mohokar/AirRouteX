# ✈️ AirRouteX

### Graph-Based Airline Route Optimization System

<p align="center">
  A Java-based airline route optimization system that models flight
  connections as a weighted graph and finds optimal routes using
  Dijkstra's Algorithm and BFS.
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Servlets](https://img.shields.io/badge/Java-Servlets-red?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JDBC](https://img.shields.io/badge/JDBC-Database-green?style=for-the-badge)
![DSA](https://img.shields.io/badge/DSA-Graphs-purple?style=for-the-badge)

</p>

---

## 📸 Application

<p align="center">
  <img src="docs/screenshots/home.png" width="850">
</p>

---

## 🚀 Overview

AirRouteX is a graph-based airline route optimization system that
connects flight data stored in MySQL with graph algorithms to find
optimal routes based on different user requirements.

The system supports:

- ✈️ Shortest distance
- 💰 Lowest price
- ⏱️ Shortest duration
- 🔄 Minimum layovers
- 🔐 User authentication
- 🎫 Flight booking
- 🛠️ Admin flight and airport management

---

## 🧠 Algorithms

| Requirement | Algorithm |
|---|---|
| Shortest Distance | Dijkstra |
| Lowest Price | Dijkstra |
| Shortest Duration | Dijkstra |
| Minimum Layovers | BFS |

Flight data is loaded from the database at query time and represented
using an adjacency-list graph.

### Graph Representation

```text
Airport
   │
   ├── Flight → Airport
   │      ├── Distance
   │      ├── Price
   │      └── Duration
   │
   └── Flight → Airport

## Folder structure
```
AirRouteX/
├── frontend/            HTML/CSS/vanilla JS (7 pages)
├── backend/
│   ├── src/com/airroutex/{models,algorithms,dao,database,controllers,servlets,utils}
│   ├── lib/              mariadb-java-client.jar, jbcrypt.jar (bundled, no Maven needed)
│   ├── web.xml
│   └── db.properties.example
├── database/
│   ├── schema.sql        3NF schema, 6 tables
│   └── sample_data.sql   7 airports, 17 flights, demo admin/user accounts
├── docs/
│   ├── ER-Diagram.png
│   ├── Architecture-Diagram.png
│   ├── Complexity-Analysis.md
│   └── Interview-QA.md
├── build.sh              compiles + assembles a deployable webapp/ folder
└── README.md
```

## Setup

1. **Database:**
   ```
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/sample_data.sql
   ```
   Demo accounts: `admin / admin123` (ADMIN), `demo / user123` (USER).

2. **Configure:**
   ```
   cp backend/db.properties.example backend/db.properties
   # edit backend/db.properties with your real MySQL credentials
   ```

3. **Build + deploy** (requires a JDK and Apache Tomcat 9 installed locally):
   ```
   export CATALINA_HOME=/path/to/tomcat9
   ./build.sh
   cp -r webapp $CATALINA_HOME/webapps/AirRouteX
   $CATALINA_HOME/bin/startup.sh
   ```
   Visit **http://localhost:8080/AirRouteX/**

`build.sh` compiles every source file, copies `mariadb-java-client.jar` + `jbcrypt.jar` into `WEB-INF/lib`, copies `db.properties` into `WEB-INF/classes`, and copies the frontend into the webapp root — the resulting `webapp/` folder is a complete, ready-to-deploy Tomcat web application.

## Why these algorithms (short version — see docs/ for full detail)

- **Dijkstra**, reused for 3 optimizations by swapping which `Flight` field counts as the edge weight — never Bellman-Ford, because flight weights are never negative.
- **BFS** for minimum layovers — every flight is an unweighted hop, so BFS's level-by-level traversal finds the fewest-flight route in O(V+E), cheaper than running Dijkstra with weight=1.
- Graph is an **adjacency list** (`HashMap<Integer, List<Flight>>`) — correct choice for a sparse network like an airline route map.
