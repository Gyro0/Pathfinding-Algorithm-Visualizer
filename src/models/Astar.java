package models;

import java.util.*;

public class Astar implements Pathfinding {
    private Grid grid;
    private Cell startCell;
    private Cell endCell;
    
    // Using LinkedList as priority queue (sorted manually by f-score)
    private LinkedList<Cell> openSet;
    private Map<Cell, Double> gScore; // Cost from start to this cell
    private Map<Cell, Double> fScore; // gScore + heuristic
    private boolean finished;
    private boolean pathFound;

    @Override
    public void init(Grid grid, Cell start, Cell goal) {
        this.grid = grid;
        this.startCell = start;
        this.endCell = goal;

        openSet = new LinkedList<>();
        gScore = new HashMap<>();
        fScore = new HashMap<>();
        finished = false;
        pathFound = false;

        // Initialize all cells
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                Cell cell = grid.getCell(r, c);
                gScore.put(cell, Double.POSITIVE_INFINITY);
                fScore.put(cell, Double.POSITIVE_INFINITY);
            }
        }

        // Start cell has g-score 0
        gScore.put(startCell, 0.0);
        fScore.put(startCell, heuristic(startCell, endCell));
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

        // Find cell with minimum f-score in openSet
        Cell current = getMinFScoreCell();
        if (current == null) {
            finished = true;
            pathFound = false;
            return true;
        }

        // Check if we reached the goal
        if (current == endCell) {
            finished = true;
            pathFound = true;
            markPath();
            return true;
        }

        openSet.remove(current);
        current.setInOpenSet(false);
        current.setInClosedSet(true);
        current.setVisited(true);

        // Process all edges from current cell
        List<Edge> edges = current.getEdges();
        if (edges != null && !edges.isEmpty()) {
            for (Edge edge : edges) {
                Cell neighbor = edge.getDestination();
                
                if (neighbor == null || neighbor.isWall() || neighbor.isInClosedSet()) {
                    continue;
                }

                // Calculate tentative g-score
                double tentativeGScore = gScore.get(current) + edge.getWeight();

                // If we found a better path to this neighbor
                if (tentativeGScore < gScore.get(neighbor)) {
                    // Update path
                    neighbor.setParent(current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, endCell));

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
     * Heuristic function: Manhattan distance
     */
    private double heuristic(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    /**
     * Finds the cell with minimum f-score in the open set.
     * This simulates a priority queue using LinkedList.
     */
    private Cell getMinFScoreCell() {
        if (openSet.isEmpty()) return null;
        
        Cell minCell = openSet.getFirst();
        double minF = fScore.get(minCell);
        
        for (Cell cell : openSet) {
            if (cell != null) {
                double f = fScore.get(cell);
                if (f < minF) {
                    minF = f;
                    minCell = cell;
                }
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
