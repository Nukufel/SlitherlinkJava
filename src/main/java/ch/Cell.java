package ch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Cell {
    private int row;
    private int col;

    private MyBoolean state = MyBoolean.FALSE;
    private Integer value;
    private Boolean showValue;

    private ArrayList<Node> cellNodes = new ArrayList<>();
    private LinkedHashMap<Location, Boarder> boarders = new LinkedHashMap<>();

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

        // Deep copy of the boarders map
        this.boarders = new LinkedHashMap<>();
        for (Location loc : other.boarders.keySet()) {
            Boarder originalBoarder = other.boarders.get(loc);
            this.boarders.put(loc, new Boarder(originalBoarder)); // Assuming Boarder has a copy constructor
        }
    }

    public void addBoarder(Location location, Boarder boarder) {
        boarders.put(location, boarder);
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

    public LinkedHashMap<Location, Boarder> getBoarders() {
        return boarders;
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

    public Boarder getBoarderByLocation(Location location) {
        return boarders.get(location);
    }

    public void calcValue(){
        var count = 0;
        for (var boarder : boarders.values()) {
            if (boarder.getResult() == MyBoolean.TRUE) {
                count++;
            }
        }
        value = count;
    }

    public boolean isCellCorrect(){
        for (var boarder : boarders.values()) {
            if (!boarder.isCorrect()) {
                return false;
            }
        }
        return true;
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

    public ArrayList<Node> getCellNodes() {
        return cellNodes;
    }

    public void setCellNodes(ArrayList<Node> cellNodes) {
        this.cellNodes = cellNodes;
    }

    public void addCellNode(Node node) {
        cellNodes.add(node);
    }
}
