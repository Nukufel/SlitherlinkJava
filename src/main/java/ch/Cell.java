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

    public ArrayList<Boarder> getBoarders() {
        return boarders;
    }

    public void setBoarders(ArrayList<Boarder> boarders) {
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

    public boolean hasBoarder() {
        return !boarders.isEmpty();
    }

    public void calcValue(){
        var count = 0;
        for (var boarder : boarders) {
            if (boarder.getState() == MyBoolean.TRUE) {
                count++;
            }
        }
        value = count;
    }

    public boolean isCellCorrect(){
        for (var boarder : boarders) {
            if (!boarder.isCorrect()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Cell cloned = (Cell) super.clone();
        cloned.boarders = new ArrayList<>();
        return cloned;
    }
}
