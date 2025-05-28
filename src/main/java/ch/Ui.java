package ch;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Line;


public class Ui extends Application {
    private Grid gameGrid;
    private int cellSize = Settings.cellSize;

    @Override
    public void start(Stage stage) throws Exception {
        gameGrid = new Grid();

        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        root.setPrefSize(Settings.gridCols * cellSize + 40, Settings.gridRows * cellSize + 40);

        drawGrid(root);

        Scene scene = new Scene(root);
        stage.setTitle("Slitherlink");
        stage.setScene(scene);
        stage.show();
    }

    public void drawGrid(Pane root) {
        Pane rects = new Pane();
        rects.setPrefSize(Settings.gridCols * cellSize, Settings.gridRows * cellSize);
        rects.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Pane lines = new Pane();
        lines.setPrefSize(Settings.gridCols * cellSize, Settings.gridRows * cellSize);

        for (int row = 0; row < Settings.gridRows; row++) {
            for (int col = 0; col < Settings.gridCols; col++) {
                Cell cell = gameGrid.getCells().get(rowColToId(row, col));

                // Position for the top-left corner of the cell
                int x = col * cellSize;
                int y = row * cellSize;

                StackPane rectWithText = new StackPane();
                Text text = new Text(cell.getValue().toString());
                text.setFill(Color.WHITE);

                if (!cell.getShowValue()){
                    //text.setText("hidden "+cell.getValue().toString());
                    text.setText("");
                }



                Rectangle cellRect = new Rectangle(x, y, cellSize, cellSize);

                if (cell.getIsInside() == MyBoolean.TRUE){
                    cellRect.setFill(Color.LIGHTPINK);
                } else {
                    cellRect.setFill(Color.LIGHTGREEN);
                }



                rectWithText.getChildren().addAll(cellRect, text);
                rectWithText.relocate(x, y);
                rects.getChildren().add(rectWithText);

                // --- Draw boarders for this cell ---
                for (Location loc : cell.getBoarders().keySet()) {
                    Boarder boarder = cell.getBoarderByLocation(loc);
                    Group group = null;

                    switch (loc) {
                        case Location.TOP ->  {
                            if (y == 0) {
                                group = addLine(x,y,x+cellSize,y, boarder, Location.TOP);
                            }
                        }
                        case Location.LEFT -> {
                            if (x == 0) {
                                group = addLine(x,y,x,y+cellSize, boarder, Location.LEFT);
                            }
                        }
                        case Location.RIGHT -> {
                            group = addLine(x+cellSize, y,x+cellSize,y+cellSize, boarder, Location.RIGHT);
                        }
                        case Location.BOTTOM -> {
                            group = addLine(x,y+cellSize,x+cellSize,y+cellSize, boarder, Location.BOTTOM);
                        }
                    }

                    if (group != null) {
                        lines.getChildren().add(group);
                    }

                }
            }
        }
        rects.relocate(20,20);
        rects.getChildren().add(lines);
        root.getChildren().add(rects);
    }

    private Group addLine(Integer x1, Integer y1, Integer x2, Integer y2, Boarder boarder, Location loc) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStrokeWidth(1);
        line.setStroke(Color.GRAY);
        line.setFill(Color.GRAY);

        Group x = createX(line, loc);

        Rectangle clickArea = createClickBox(x1, y1, x2, y2, loc);
        clickArea.setOnMouseClicked(event -> handleCellClick(boarder, line, x));

        return new Group(line, x, clickArea);
    }

    public Group createX(Line line, Location loc) {
        int halveCellSize = cellSize / 2;
        int crossOffset = cellSize / 12;

        if (loc == Location.TOP || loc == Location.BOTTOM) {
            Line x1 = new Line(line.getStartX() + halveCellSize - crossOffset , line.getStartY() - crossOffset, line.getEndX() - halveCellSize + crossOffset, line.getEndY() + crossOffset);
            Line x2 = new Line(line.getStartX() + halveCellSize - crossOffset, line.getStartY() + crossOffset, line.getEndX() - halveCellSize + crossOffset, line.getEndY() - crossOffset);
            x1.setStroke(Color.TRANSPARENT);
            x2.setStroke(Color.TRANSPARENT);
            return new Group(x1, x2);
        } else {
            Line x1 = new Line(line.getStartX() + crossOffset, line.getStartY() + halveCellSize - crossOffset, line.getEndX() - crossOffset, line.getEndY() - halveCellSize + crossOffset);
            Line x2 = new Line(line.getStartX() - crossOffset, line.getStartY() + halveCellSize - crossOffset, line.getEndX() + crossOffset, line.getEndY() - halveCellSize + crossOffset);
            x1.setStroke(Color.TRANSPARENT);
            x2.setStroke(Color.TRANSPARENT);
            return new Group(x1, x2);
        }
    }

    private Rectangle createClickBox(Integer x1, Integer y1, Integer x2, Integer y2, Location loc) {
        double thickness = cellSize / 2;

        Rectangle clickArea;
        if (loc == Location.TOP || loc == Location.BOTTOM) {
            clickArea = new Rectangle(x1, y1 - thickness/2, cellSize, thickness);
        } else {
            clickArea = new Rectangle(x1 - thickness/2, y1, thickness, cellSize);
        }
        clickArea.setFill(Color.TRANSPARENT);
        clickArea.setArcWidth(thickness * 2);
        clickArea.setArcHeight(thickness * 2);

        return clickArea;
    }



    private void handleCellClick(Boarder boarder, javafx.scene.shape.Line line, Group xGroup) {
        boarder.toggleBoarder();
        MyBoolean state = boarder.getState();
        switch (state) {
            case MyBoolean.TRUE:
                line.setStrokeWidth(3);
                line.setStroke(Color.BLUE);
                line.setFill(Color.BLUE);
                setXs(xGroup, false);
                break;
            case MyBoolean.FALSE:
                line.setStrokeWidth(1);
                line.setStroke(Color.GRAY);
                line.setFill(Color.GRAY);
                setXs(xGroup, true);
                break;
            case MyBoolean.NULL:
                line.setStrokeWidth(1);
                line.setStroke(Color.GRAY);
                line.setFill(Color.GRAY);

                setXs(xGroup, false);
                break;
        }
        if (gameGrid.isSolved()){
            System.out.println("Solved");
        }

    }

    public void setXs(Group group, boolean state) {
        for (Node x : group.getChildren()){
            if (x.getClass() == Line.class){
                if (state){
                    ((Line) x).setStroke(Color.RED);
                } else {
                    ((Line) x).setStroke(Color.TRANSPARENT);
                }
            }
        }
    }


    public static int rowColToId(int row, int col) {
        return row * Settings.gridRows + col;
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}