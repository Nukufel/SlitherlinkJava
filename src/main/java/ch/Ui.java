package ch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Ui extends Application {
    private Grid gameGrid;
    private int cellSize = Settings.cellSize;

    @Override
    public void start(Stage stage) throws Exception {
        gameGrid = new Grid();

        Pane root = new Pane();
        root.setPrefSize(Settings.gridCols * cellSize, Settings.gridRows * cellSize);

        drawGrid(root);

        Scene scene = new Scene(root);
        stage.setTitle("Slitherlink");
        stage.setScene(scene);
        stage.show();
    }

    public void drawGrid(Pane root) {
        for (int row = 0; row < Settings.gridRows; row++) {
            for (int col = 0; col < Settings.gridCols; col++) {
                // Draw the cell as a rectangle
                Rectangle cellRect = new Rectangle(col * cellSize, row * cellSize, cellSize, cellSize);
                cellRect.setFill(Color.WHITE);
                cellRect.setStroke(Color.BLACK); // Border color

                // Add the cell to the pane
                root.getChildren().add(cellRect);

                // Add mouse click event to handle cell interaction
                int finalRow = row;
                int finalCol = col;
                cellRect.setOnMouseClicked(event -> handleCellClick(finalRow, finalCol));
            }
        }
    }

    private void handleCellClick(int row, int col) {
        // Handle the logic for when a cell is clicked
        System.out.println("Cell clicked: Row " + row + ", Col " + col);

        // Here you can call your game logic to toggle or modify cell states.
        // Example: gameGrid.toggleCellState(row, col);

        // After modifying the cell, you can call a repaint method
        // This would involve calling the pane's getChildren() and updating cell display.
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}