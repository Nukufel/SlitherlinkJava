package ch;

import javafx.util.Pair;

import java.util.*;

public class Cell {
    private final int row;
    private final int col;

    private MyBoolean state = MyBoolean.FALSE;
    private Integer value;
    private Boolean showValue;

    private LinkedHashSet<Node> cellNodes = new LinkedHashSet<>();
    private LinkedHashMap<Location, Border> borders = new LinkedHashMap<>();

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        value = null;
        showValue = true;
    }

    public Cell(Cell other) {
        this.row = other.row;
        this.col = other.col;
        this.value = other.value;
        this.showValue = other.showValue;
        this.state = other.state; // Assuming MyBoolean is immutable
        this.cellNodes = new LinkedHashSet<>();

        // Deep copy of the boarders map
        this.borders = new LinkedHashMap<>();



    }

    public void addBoarder(Location location, Border border) {
        borders.put(location, border);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public MyBoolean getState() {
        return state;
    }

    public void setState(MyBoolean state) {
        this.state = state;
    }

    public LinkedHashMap<Location, Border> getBoarders() {
        return borders;
    }

    public Boolean getShowValue() {
        return showValue;
    }

    public void setShowValue(Boolean showValue) {
        this.showValue = showValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public boolean hasValue(){
        return value != null;
    }

    public Border getBoarderByLocation(Location location) {
        return borders.get(location);
    }


    public void calcValue(){
        var count = 0;
        for (var boarder : borders.values()) {
            if (boarder.getResult() == MyBoolean.TRUE) {
                count++;
            }
        }
        value = count;
    }

    public boolean isCellCorrect(){
        for (var boarder : borders.values()) {
            if (!boarder.isCorrect()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        int count = 0;
        for (Border border : borders.values()) {
            if (border.getResult() == MyBoolean.TRUE) {
                count ++;
            }
        }
        if (count >= value) {
            return true;
        }
        return false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell cell)) return false;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }



    public void addCellNode(Node node) {
        cellNodes.add(node);
    }
}
