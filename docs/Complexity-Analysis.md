# Complexity Analysis

Let V = number of airports (vertices), E = number of flights (edges).

| Component | Time | Space | Why |
|---|---|---|---|
| GraphBuilder.buildGraph | O(E) | O(V+E) | one addEdge() per flight |
| BFSPathFinder (min layovers) | O(V+E) | O(V) | each vertex/edge visited once; queue+visited+previous maps |
| DijkstraPathFinder (distance/price/duration) | O((V+E) log V) | O(V+E) | PriorityQueue insert/extract = O(log V); distance/previous maps + adjacency list |
| PasswordUtil (BCrypt hash/verify) | O(2^cost) per call | O(1) | deliberately slow (cost factor 10) to resist brute force |
| FlightDAO.getAllFlights | O(E) rows returned | O(E) | one JDBC round trip, indexed on source/destination |

**Why Dijkstra, not Bellman-Ford:** all edge weights (distance, price, duration) are always ≥ 0 for a real flight, so Dijkstra's greedy choice is always safe and correct. Bellman-Ford (O(V·E), much slower) exists specifically to handle negative weights, which never occur here — using it would only add cost with no benefit.

**Why BFS, not Dijkstra, for minimum layovers:** "fewest hops" is exactly the unweighted shortest-path problem BFS solves in O(V+E). Modeling every flight as weight 1 and running Dijkstra would give the same answer but pay for a priority queue (O(log V) per op) for no reason.
