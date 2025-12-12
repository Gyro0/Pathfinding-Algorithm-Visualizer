package views;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import java.util.List;


import models.*;


public class PathApp extends Application {

    private int ROWS = 40;
    private int COLS = 60;
    private static final double CELL_SIZE = 15;

    private TextField startField;
    private TextField goalField;
    private Label pathCostLabel;

    private Grid grid;
    private Rectangle[][] rects;
    private Pane gridPane;

    private Pathfinding algorithm;
    private Timeline timeline;
    private boolean weightsVisible = false;
    private double animationSpeed = 10; // Default speed in milliseconds
    
    private Cell startCell;
    private Cell goalCell;
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        grid = new Grid(ROWS, COLS);

        gridPane = createGridPane();
        
        // Initialize start and goal cells
        startCell = grid.getCell(0, 0);
        goalCell = grid.getCell(10, 10);

        // --- Create right sidebar ---
        VBox sidebar = createSidebar();
        
        // Wrap sidebar in ScrollPane
        ScrollPane scrollPane = new ScrollPane(sidebar);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root = new BorderPane();
        root.setCenter(gridPane);
        root.setRight(scrollPane);

        Scene scene = new Scene(root, 1200, 650);
        stage.setScene(scene);
        stage.setTitle("Pathfinding Visualizer");
        stage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 0 0 0 2;");

        // --- Title ---
        Label titleLabel = new Label("Pathfinding Controls");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // --- Coordinates Section ---
        Label coordsLabel = new Label("Coordinates");
        coordsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        startField = new TextField("0,0");
        startField.setPromptText("Start (r,c)");
        startField.setPrefWidth(200);

        goalField = new TextField("10,10");
        goalField.setPromptText("Goal (r,c)");
        goalField.setPrefWidth(200);

        VBox coordsBox = new VBox(8,
                new Label("Start (row, col):"), startField,
                new Label("Goal (row, col):"), goalField
        );
        
        Separator sep1 = new Separator();

        // --- Grid Size Section ---
        Label gridSizeLabel = new Label("Grid Size");
        gridSizeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<String> gridSizeCombo = new ComboBox<>();
        gridSizeCombo.getItems().addAll(
                "Small (20x30)",
                "Medium (40x60)",
                "Large (60x90)",
                "Extra Large (80x120)"
        );
        gridSizeCombo.setValue("Medium (40x60)");
        gridSizeCombo.setPrefWidth(200);

        Button applyGridSizeButton = new Button("Apply Grid Size");
        applyGridSizeButton.setPrefWidth(200);
        applyGridSizeButton.setOnAction(e -> {
            String selected = gridSizeCombo.getValue();
            if (selected.contains("20x30")) {
                ROWS = 20; COLS = 30;
            } else if (selected.contains("40x60")) {
                ROWS = 40; COLS = 60;
            } else if (selected.contains("60x90")) {
                ROWS = 60; COLS = 90;
            } else if (selected.contains("80x120")) {
                ROWS = 80; COLS = 120;
            }
            regenerateGrid();
        });

        VBox gridSizeBox = new VBox(8, gridSizeCombo, applyGridSizeButton);

        Separator sep2 = new Separator();

        // --- Speed Control Section ---
        Label speedLabel = new Label("Animation Speed");
        speedLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label speedValueLabel = new Label("Speed: Medium");
        speedValueLabel.setStyle("-fx-font-size: 12px;");

        Slider speedSlider = new Slider(1, 100, 10);
        speedSlider.setPrefWidth(200);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(false);
        speedSlider.setMajorTickUnit(25);
        speedSlider.setBlockIncrement(10);

        // Update speed when slider changes
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationSpeed = 101 - newVal.doubleValue(); // Invert so right = faster
            String speedText;
            if (animationSpeed > 75) {
                speedText = "Speed: Very Slow";
            } else if (animationSpeed > 50) {
                speedText = "Speed: Slow";
            } else if (animationSpeed > 25) {
                speedText = "Speed: Medium";
            } else if (animationSpeed > 10) {
                speedText = "Speed: Fast";
            } else {
                speedText = "Speed: Very Fast";
            }
            speedValueLabel.setText(speedText);
        });

        VBox speedBox = new VBox(8, speedValueLabel, speedSlider);

        Separator sep3 = new Separator();

        // --- Algorithms Section ---
        Label algoLabel = new Label("Algorithms");
        algoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button runBfsButton = new Button("Run BFS");
        runBfsButton.setPrefWidth(200);
        runBfsButton.setOnAction(e -> startBfs());

        Button runDfsButton = new Button("Run DFS");
        runDfsButton.setPrefWidth(200);
        runDfsButton.setOnAction(e -> startDfs());

        Button runDijkstraButton = new Button("Run Dijkstra");
        runDijkstraButton.setPrefWidth(200);
        runDijkstraButton.setOnAction(e -> startDijkstra());

        Button runAstarButton = new Button("Run A*");
        runAstarButton.setPrefWidth(200);
        runAstarButton.setOnAction(e -> startAstar());

        Separator sep4 = new Separator();

        // --- Tools Section ---
        Label toolsLabel = new Label("Tools");
        toolsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button generateMazeButton = new Button("Generate Maze");
        generateMazeButton.setPrefWidth(200);
        generateMazeButton.setOnAction(e -> generateRandomMaze());

        Button toggleWeightsButton = new Button("Show/Hide Weights");
        toggleWeightsButton.setPrefWidth(200);
        toggleWeightsButton.setOnAction(e -> toggleEdgeWeights());

        Button resetButton = new Button("Reset Grid");
        resetButton.setPrefWidth(200);
        resetButton.setOnAction(e -> resetGrid());
        resetButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");

        Separator sep5 = new Separator();

        // --- Path Cost Label ---
        pathCostLabel = new Label("Path Cost: -");
        pathCostLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        pathCostLabel.setWrapText(true);

        // Add all components to sidebar
        sidebar.getChildren().addAll(
                titleLabel,
                new Separator(),
                coordsLabel,
                coordsBox,
                sep1,
                gridSizeLabel,
                gridSizeBox,
                sep2,
                speedLabel,
                speedBox,
                sep3,
                algoLabel,
                runBfsButton,
                runDfsButton,
                runDijkstraButton,
                runAstarButton,
                sep4,
                toolsLabel,
                generateMazeButton,
                toggleWeightsButton,
                resetButton,
                sep5,
                pathCostLabel
        );

        return sidebar;
    }

    private Pane createGridPane() {
        Pane pane = new Pane();
        pane.setPrefSize(COLS * CELL_SIZE, ROWS * CELL_SIZE);

        rects = new Rectangle[ROWS][COLS];

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setLayoutX(c * CELL_SIZE);
                rect.setLayoutY(r * CELL_SIZE);
                rect.setStroke(Color.DARKGRAY);
                rect.setFill(Color.LIGHTGRAY);

                final int row = r;
                final int col = c;

                rect.setOnMouseClicked(e -> {
                    Cell cell = grid.getCell(row, col);
                    
                    if (e.isShiftDown()) {
                        // Shift+Click: cycle through terrain types
                        cycleTerrain(cell);
                        // Rebuild edges when terrain changes
                        grid.rebuildEdges();
                    } else {
                        // Regular click: toggle wall
                        boolean wasWall = cell.isWall();
                        cell.setWall(!wasWall);
                        
                        if (!wasWall) {
                            // Cell became a wall - remove its edges
                            cell.clearEdges();
                            // Remove edges from other cells pointing to this cell
                            for (int r2 = 0; r2 < ROWS; r2++) {
                                for (int c2 = 0; c2 < COLS; c2++) {
                                    Cell otherCell = grid.getCell(r2, c2);
                                    otherCell.getEdges().removeIf(edge -> edge.getDestination() == cell);
                                }
                            }
                        } else {
                            // Cell is no longer a wall - reset to normal terrain and rebuild edges
                            cell.setTerrain(Cell.TerrainType.NORMAL);
                            grid.rebuildEdges();
                        }
                    }
                    refreshGrid();
                    // Refresh edge weights if they're visible
                    if (weightsVisible) {
                        gridPane.getChildren().removeIf(node -> node instanceof Text);
                        drawEdgeWeights();
                    }
                });
                rects[r][c] = rect;
                pane.getChildren().add(rect);
            }
        }
        
        return pane;
    }

    private void toggleEdgeWeights() {
        // Remove all existing weight labels
        gridPane.getChildren().removeIf(node -> node instanceof Text);
        
        // Toggle visibility state
        weightsVisible = !weightsVisible;
        
        if (weightsVisible) {
            drawEdgeWeights();
        }
    }

    private void drawEdgeWeights() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid.getCell(r, c);
                
                // Skip if cell is a wall
                if (cell.isWall()) continue;
                
                for (Edge edge : cell.getEdges()) {
                    Cell neighbor = edge.getDestination();
                    
                    // Skip if neighbor is a wall
                    if (neighbor.isWall()) continue;
                    
                    double weight = edge.getWeight();
                    
                    // Calculate midpoint between cells for label placement
                    double x1 = c * CELL_SIZE + CELL_SIZE / 2;
                    double y1 = r * CELL_SIZE + CELL_SIZE / 2;
                    double x2 = neighbor.getCol() * CELL_SIZE + CELL_SIZE / 2;
                    double y2 = neighbor.getRow() * CELL_SIZE + CELL_SIZE / 2;
                    
                    double midX = (x1 + x2) / 2;
                    double midY = (y1 + y2) / 2;
                    
                    // Create weight label
                    Text weightLabel = new Text(String.format("%.0f", weight));
                    weightLabel.setFont(new Font(8));
                    weightLabel.setFill(Color.DARKRED);
                    weightLabel.setX(midX - 4);
                    weightLabel.setY(midY + 3);
                    
                    gridPane.getChildren().add(weightLabel);
                }
            }
        }
    }

    private void startBfs() {
        // Stop any running algorithm
        if (timeline != null) {
            timeline.stop();
        }
        
        // Reset search state but keep walls and edges
        resetSearchState();
        
        Cell defaultStart = grid.getCell(0, 0);
        Cell defaultGoal  = grid.getCell(10, 10);

        Cell start = getCellFromField(startField, defaultStart);
        Cell goal  = getCellFromField(goalField,  defaultGoal);
        
        // Update start and goal cells
        startCell = start;
        goalCell = goal;

        algorithm = new BFS();  // your BFS class implementing Pathfinding
        algorithm.init(grid, start, goal);

        refreshGrid();
        startTimeline();
    }

    private void startDfs() {
        // Stop any running algorithm
        if (timeline != null) {
            timeline.stop();
        }
        
        // Reset search state but keep walls and edges
        resetSearchState();
        
        Cell defaultStart = grid.getCell(0, 0);
        Cell defaultGoal  = grid.getCell(10, 10);

        Cell start = getCellFromField(startField, defaultStart);
        Cell goal  = getCellFromField(goalField,  defaultGoal);
        
        // Update start and goal cells
        startCell = start;
        goalCell = goal;

        algorithm = new DFS();  // your DFS class implementing Pathfinding
        algorithm.init(grid, start, goal);

        refreshGrid();
        startTimeline();
    }

    private void startDijkstra() {
        // Stop any running algorithm
        if (timeline != null) {
            timeline.stop();
        }
        
        // Reset search state but keep walls and edges
        resetSearchState();
        
        Cell defaultStart = grid.getCell(0, 0);
        Cell defaultGoal  = grid.getCell(10, 10);

        Cell start = getCellFromField(startField, defaultStart);
        Cell goal  = getCellFromField(goalField,  defaultGoal);
        
        // Update start and goal cells
        startCell = start;
        goalCell = goal;

        algorithm = new Dijkstra();  // Dijkstra's algorithm
        algorithm.init(grid, start, goal);

        refreshGrid();
        startTimeline();
    }

    private void startAstar() {
        // Stop any running algorithm
        if (timeline != null) {
            timeline.stop();
        }
        
        // Reset search state but keep walls and edges
        resetSearchState();
        
        Cell defaultStart = grid.getCell(0, 0);
        Cell defaultGoal  = grid.getCell(10, 10);

        Cell start = getCellFromField(startField, defaultStart);
        Cell goal  = getCellFromField(goalField,  defaultGoal);
        
        // Update start and goal cells
        startCell = start;
        goalCell = goal;

        algorithm = new Astar();  // A* algorithm
        algorithm.init(grid, start, goal);

        refreshGrid();
        startTimeline();
    }

    private void startTimeline() {
        if (timeline != null) {
            timeline.stop();
        }

        pathCostLabel.setText("Path Cost: Searching...");

        timeline = new Timeline(new KeyFrame(Duration.millis(animationSpeed), e -> {
            boolean finished = algorithm.step();
            refreshGrid();
            if (finished) {
                timeline.stop();
                updatePathCost();
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updatePathCost() {
        if (algorithm != null && algorithm.hasPath()) {
            double totalCost = 0;
            List<Cell> path = algorithm.getPath();
            
            // Calculate total cost by summing edge weights between consecutive cells in path
            for (int i = 0; i < path.size() - 1; i++) {
                Cell current = path.get(i);
                Cell next = path.get(i + 1);
                
                // Find the edge from current to next
                for (Edge edge : current.getEdges()) {
                    if (edge.getDestination() == next) {
                        totalCost += edge.getWeight();
                        break;
                    }
                }
            }
            
            // Number of steps = number of edges = path.size() - 1
            int steps = path.size() - 1;
            pathCostLabel.setText(String.format("Path Cost: %.1f (Steps: %d)", totalCost, steps));
        } else if (algorithm != null) {
            pathCostLabel.setText("Path Cost: No path found!");
        }
    }

    private void resetSearchState() {
        // Clear only search state, keep walls and edges intact
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid.getCell(r, c).resetSearchState();
            }
        }
        pathCostLabel.setText("Path Cost: -");
    }

    private void resetGrid() {
        // Stop animation if running
        if (timeline != null) {
            timeline.stop();
        }

        // Clear everything - walls, terrain, and search state
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid.getCell(r, c);
                cell.setWall(false);
                cell.setTerrain(Cell.TerrainType.NORMAL);
                cell.resetSearchState();
            }
        }
        
        // Rebuild all edges since walls were cleared
        grid.rebuildEdges();
        
        // Reset to default start and goal
        startCell = grid.getCell(0, 0);
        goalCell = grid.getCell(10, 10);
        startField.setText("0,0");
        goalField.setText("10,10");

        algorithm = null;
        pathCostLabel.setText("Path Cost: -");
        
        // Clear edge weights if visible
        if (weightsVisible) {
            gridPane.getChildren().removeIf(node -> node instanceof Text);
            drawEdgeWeights();
        }
        
        refreshGrid();
    }

    private void cycleTerrain(Cell cell) {
        Cell.TerrainType current = cell.getTerrain();
        Cell.TerrainType[] types = Cell.TerrainType.values();
        
        // Find next terrain type (excluding WALL, use regular click for walls)
        int currentIndex = current.ordinal();
        int nextIndex = (currentIndex + 1) % (types.length - 1); // Skip WALL
        
        cell.setTerrain(types[nextIndex]);
    }

    private void refreshGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid.getCell(r, c);
                Rectangle rect = rects[r][c];

                // Priority: start/goal > path > closed > open > terrain
                if (cell == startCell) {
                    rect.setFill(Color.ORANGE);
                } else if (cell == goalCell) {
                    rect.setFill(Color.PURPLE);
                } else if (cell.isInPath()) {
                    rect.setFill(Color.RED);
                } else if (cell.isInClosedSet()) {
                    rect.setFill(Color.LIGHTGREEN);
                } else if (cell.isInOpenSet()) {
                    rect.setFill(Color.LIGHTBLUE);
                } else if (cell.isWall()) {
                    rect.setFill(Color.BLACK);
                } else {
                    // Show terrain color
                    rect.setFill(getTerrainColor(cell.getTerrain()));
                }
            }
        }
    }

    private Color getTerrainColor(Cell.TerrainType terrain) {
        switch (terrain) {
            case NORMAL:   return Color.WHITE;
            case SAND:     return Color.LIGHTYELLOW;
            case WATER:    return Color.LIGHTBLUE;
            case MOUNTAIN: return Color.SADDLEBROWN;
            case WALL:     return Color.BLACK;
            default:       return Color.LIGHTGRAY;
        }
    }

    private void generateRandomMaze() {
        // Stop any running algorithm
        if (timeline != null) {
            timeline.stop();
        }
        
        // Clear the grid and reset algorithm
        algorithm = null;
        pathCostLabel.setText("Path Cost: -");
        
        // Reset all cells
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid.getCell(r, c);
                cell.setWall(false);
                cell.setTerrain(Cell.TerrainType.NORMAL);
                cell.resetSearchState();
            }
        }
        
        // Generate random walls (30% chance)
        java.util.Random random = new java.util.Random();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (random.nextDouble() < 0.3) {
                    grid.getCell(r, c).setWall(true);
                }
            }
        }
        
        // Rebuild edges after wall generation
        grid.rebuildEdges();
        
        // Select random start and goal that are not walls
        Cell start = null;
        Cell goal = null;
        
        while (start == null || start.isWall()) {
            int r = random.nextInt(ROWS);
            int c = random.nextInt(COLS);
            start = grid.getCell(r, c);
        }
        
        while (goal == null || goal.isWall() || goal == start) {
            int r = random.nextInt(ROWS);
            int c = random.nextInt(COLS);
            goal = grid.getCell(r, c);
        }
        
        // Update start and goal cell references
        startCell = start;
        goalCell = goal;
        
        // Update text fields with new coordinates
        startField.setText(start.getRow() + "," + start.getCol());
        goalField.setText(goal.getRow() + "," + goal.getCol());
        
        // Refresh display
        refreshGrid();
        
        // Refresh edge weights if they're visible
        if (weightsVisible) {
            gridPane.getChildren().removeIf(node -> node instanceof Text);
            drawEdgeWeights();
        }
    }

    private Cell getCellFromField(TextField field, Cell defaultCell) {
        try {
            String text = field.getText().trim();
            String[] parts = text.split(",");
            if (parts.length != 2) return defaultCell;

            int row = Integer.parseInt(parts[0].trim());
            int col = Integer.parseInt(parts[1].trim());

            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                return defaultCell; // out of bounds → fallback
            }

            return grid.getCell(row, col);
        } catch (Exception e) {
            // any parse error → fallback
            return defaultCell;
        }
    }

    private void regenerateGrid() {
        // Stop any running algorithm
        if (timeline != null) {
            timeline.stop();
        }
        
        // Clear edge weights if visible
        if (weightsVisible) {
            gridPane.getChildren().removeIf(node -> node instanceof Text);
        }
        
        // Create new grid with new dimensions
        grid = new Grid(ROWS, COLS);
        
        // Remove old grid pane and create new one
        root.setCenter(null);
        gridPane = createGridPane();
        root.setCenter(gridPane);
        
        // Reset start and goal to defaults
        startCell = grid.getCell(0, 0);
        goalCell = grid.getCell(Math.min(10, ROWS - 1), Math.min(10, COLS - 1));
        startField.setText("0,0");
        goalField.setText(Math.min(10, ROWS - 1) + "," + Math.min(10, COLS - 1));
        
        // Reset algorithm and path cost
        algorithm = null;
        pathCostLabel.setText("Path Cost: -");
        
        // Redraw edge weights if they were visible
        if (weightsVisible) {
            drawEdgeWeights();
        }
        
        refreshGrid();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
