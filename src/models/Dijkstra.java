package models;

import java.util.*;

public class Dijkstra implements Pathfinding {
    private Grid grid;
    private Cell startCell;
    private Cell endCell;
    
    // Using LinkedList as priority queue (sorted manually)
    private LinkedList<Cell> openSet;
    private boolean finished;
    private boolean pathFound;

    @Override
    public void init(Grid grid, Cell start, Cell goal) {
        this.grid = grid;
        this.startCell = start;
        this.endCell = goal;

        openSet = new LinkedList<>();
        finished = false;
        pathFound = false;

        // Initialize all cells with infinite distance
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                grid.getCell(r, c).setDistance(Double.POSITIVE_INFINITY);
            }
        }

        // Start cell has distance 0
        startCell.setDistance(0);
        startCell.setInOpenSet(true);
        openSet.add(startCell);
    }

    @Override
    public boolean step() {
        if (finished) return true;

        if (openSet.isEmpty()) {
            finished = true;
            pathFound = false;
            return true;
        }

        // Find cell with minimum distance in openSet (manual priority queue)
        Cell current = getMinDistanceCell();
        if (current == null) {
            finished = true;
            pathFound = false;
            return true;
        }
        
        openSet.remove(current);
        current.setInOpenSet(false);
        current.setInClosedSet(true);
        current.setVisited(true);

        // Check if we reached the goal
        if (current == endCell) {
            finished = true;
            pathFound = true;
            markPath();
            return true;
        }

        // Process all edges from current cell
        List<Edge> edges = current.getEdges();
        if (edges != null && !edges.isEmpty()) {
            for (Edge edge : edges) {
                Cell neighbor = edge.getDestination();
                
                if (neighbor == null || neighbor.isWall() || neighbor.isInClosedSet()) {
                    continue;
                }

                // Calculate new distance using edge weight
                double edgeCost = edge.getWeight();
                double newDistance = current.getDistance() + edgeCost;

                // If we found a shorter path to this neighbor
                if (newDistance < neighbor.getDistance()) {
                    neighbor.setDistance(newDistance);
                    neighbor.setParent(current);

                    // Add to open set if not already there
                    if (!neighbor.isInOpenSet()) {
                        neighbor.setInOpenSet(true);
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return false; // not finished yet
    }

    /**
     * Finds the cell with minimum distance in the open set.
     * This simulates a priority queue using LinkedList.
     */
    private Cell getMinDistanceCell() {
        if (openSet.isEmpty()) return null;
        
        Cell minCell = openSet.getFirst();
        for (Cell cell : openSet) {
            if (cell != null && cell.getDistance() < minCell.getDistance()) {
                minCell = cell;
            }
        }
        return minCell;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean hasPath() {
        return pathFound;
    }

    @Override
    public List<Cell> getPath() {
        if (!pathFound) return Collections.emptyList();

        List<Cell> path = new ArrayList<>();
        Cell current = endCell;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private void markPath() {
        for (Cell cell : getPath()) {
            cell.setInPath(true);
        }
    }
}

