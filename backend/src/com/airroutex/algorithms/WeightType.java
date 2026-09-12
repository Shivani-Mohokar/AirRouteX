package com.airroutex.algorithms;

/**
 * WeightType.java
 * ------------------------------------------------------------------
 * Which Flight field DijkstraPathFinder should treat as the edge
 * weight. This is what lets ONE Dijkstra implementation serve THREE
 * different optimization goals - only the number being compared
 * changes, the algorithm itself does not.
 * ------------------------------------------------------------------
 */
public enum WeightType {
    DISTANCE,
    PRICE,
    DURATION
}
