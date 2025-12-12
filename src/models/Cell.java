package models;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    public enum TerrainType {
        NORMAL(1.0),      // White - cost 1
        SAND(2.0),        // Yellow - cost 2
        WATER(5.0),       // Blue - cost 5
        MOUNTAIN(10.0),   // Brown - cost 10
        WALL(Double.POSITIVE_INFINITY);  // Black - impassable

        private final double cost;

        TerrainType(double cost) {
            this.cost = cost;
        }

        public double getCost() {
            return cost;
        }
    }

    private final int row;
    private final int col;

    private TerrainType terrain;
    private boolean wall;
    private boolean visited;
    private boolean inOpenSet;
    private boolean inClosedSet;
    private boolean inPath;

    private Cell parent;
    private double distance;
    
    // Adjacency list: edges to neighbors
    private List<Edge> edges;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.terrain = TerrainType.NORMAL;
        this.edges = new ArrayList<>();
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public boolean isWall() {
        return wall;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
        if (wall) {
            this.terrain = TerrainType.WALL;
        }
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
        this.wall = (terrain == TerrainType.WALL);
    }

    public double getTerrainCost() {
        return terrain.getCost();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isInOpenSet() {
        return inOpenSet;
    }

    public void setInOpenSet(boolean inOpenSet) {
        this.inOpenSet = inOpenSet;
    }

    public boolean isInClosedSet() {
        return inClosedSet;
    }

    public void setInClosedSet(boolean inClosedSet) {
        this.inClosedSet = inClosedSet;
    }

    public boolean isInPath() {
        return inPath;
    }

    public void setInPath(boolean inPath) {
        this.inPath = inPath;
    }

    public Cell getParent() {
        return parent;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public void clearEdges() {
        this.edges.clear();
    }

    public void resetSearchState() {
        visited = false;
        inOpenSet = false;
        inClosedSet = false;
        inPath = false;
        parent = null;
        distance = Double.POSITIVE_INFINITY;
    }

}
