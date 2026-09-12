# AirRouteX — Graph-Based Airline Route Optimization System

Java Servlets + JDBC + MySQL. Graph modeled from the Flight table at query time (never hardcoded); Dijkstra (shortest distance / lowest price / shortest duration) and BFS (minimum layovers) implemented from scratch on `HashMap`/`ArrayList`/`java.util.Queue`/`java.util.PriorityQueue`. No Spring, no React, no JWT — plain `HttpSession` auth with BCrypt-hashed passwords.

**Verification performed before packaging:** every `.java` file compiles cleanly against the real `javax.servlet-api`, MariaDB JDBC driver, and jBCrypt jars (bundled in `backend/lib/`). The full pipeline — MySQL → JDBC → Graph → Dijkstra/BFS → auth (register/login/bcrypt) → transactional booking (Booking+BookingLeg) → admin CRUD → unreachable-route handling — was run end-to-end against a live MariaDB instance with the real `sample_data.sql` loaded; see `docs/` for the diagrams this produced.

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
