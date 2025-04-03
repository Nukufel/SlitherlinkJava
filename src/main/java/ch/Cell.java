package ch;

import java.util.ArrayList;
import java.util.HashMap;

public class Cell {
    private int row;
    private int col;
    private int id;
    private MyBoolean isInside;
    private Integer value;
    private Boolean showValue;

    private HashMap<Location, Boarder> boarders = new HashMap<>();

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        id = row + col;
        value = null;
        showValue = true;
    }

    public void addBoarder(Location location, Boarder boarder) {
        boarders.put(location, boarder);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public MyBoolean getIsInside() {
        return isInside;
    }

    public void setIsInside(MyBoolean isInside) {
        this.isInside = isInside;
    }

    public HashMap<Location, Boarder> getBoarders() {
        return boarders;
    }

    public void setBoarders(HashMap<Location, Boarder> boarders) {
        this.boarders = boarders;
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

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean hasValue(){
        return value != null;
    }

    public boolean hasBoarder() {
        return !boarders.isEmpty();
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
}
