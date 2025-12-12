package models;
import java.util.*;

public class Grid {
    private final int rows;
    private final int cols;
    private final Cell[][] cells;
    private final Random random;

    public  Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells= new Cell[rows][cols];
        this.random = new Random();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                this.cells[i][j]=new Cell(i,j);
            }
        }
        // Build edges after all cells are created
        buildEdges();
    }
    
    /**
     * Builds edges between cells based on adjacency.
     * Randomly creates edges to 4-directional neighbors.
     * Random weight 0-10: if 0, no edge is created.
     */
    private void buildEdges() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                
                // Add edges to 4-directional neighbors with random weights
                // Left
                if (inBounds(r, c - 1)) {
                    Cell neighbor = cells[r][c - 1];
                    int weight = random.nextInt(1,10); // Random weight 0-10
                    if (weight > 0) {
                        cell.addEdge(new Edge(neighbor, weight));
                    }
                }
                // Right
                if (inBounds(r, c + 1)) {
                    Cell neighbor = cells[r][c + 1];
                    int weight = random.nextInt(1,10); // Random weight 0-10
                    if (weight > 0) {
                        cell.addEdge(new Edge(neighbor, weight));
                    }
                }
                // Up
                if (inBounds(r - 1, c)) {
                    Cell neighbor = cells[r - 1][c];
                    int weight = random.nextInt(1,10); // Random weight 0-10
                    if (weight > 0) {
                        cell.addEdge(new Edge(neighbor, weight));
                    }
                }
                // Down
                if (inBounds(r + 1, c)) {
                    Cell neighbor = cells[r + 1][c];
                    int weight = random.nextInt(1,10); // Random weight 0-10
                    if (weight > 0) {
                        cell.addEdge(new Edge(neighbor, weight));
                    }
                }
            }
        }
    }
    
    /**
     * Rebuilds all edges. Call this when terrain costs change.
     */
    public void rebuildEdges() {
        // Clear existing edges
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].clearEdges();
            }
        }
        // Rebuild
        buildEdges();
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public Cell[][] getCells() {
        return cells;
    }
    public Cell getCell(int i, int j) {
        if(!inBounds(i,j)) return null;
        return cells[i][j];
    }

    public boolean inBounds(int i, int j) {
        return i>=0 && i<rows && j>=0 && j<cols;
    }

    public List<Cell> getNeighbors(Cell cell) {

        if (inBounds(cell.getRow(), cell.getCol())){

            List<Cell> res = new ArrayList<>();
            if (inBounds( cell.getRow(), cell.getCol() - 1)) {
                res.add(cells[cell.getRow()][cell.getCol() - 1]);
            }
            if (inBounds( cell.getRow(), cell.getCol() + 1)) {
                res.add(cells[cell.getRow()][cell.getCol() + 1]);
            }
            if (inBounds( cell.getRow()-1, cell.getCol())) {
                res.add(cells[cell.getRow() - 1][cell.getCol()]);
            }
            if (inBounds( cell.getRow()+1, cell.getCol())) {
                res.add(cells[cell.getRow() + 1][cell.getCol()]);
            }
            return res;
        }
        return null;
    }
    public void resetSearchState() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].resetSearchState();
            }
        }
    }



}
