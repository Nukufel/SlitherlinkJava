package ch;

import java.util.HashMap;

public class Cell {
    private int id;
    private MyBoolean isInside;
    private Integer value;
    private Boolean showValue;

    private HashMap<Location, Boarder> boarders = new HashMap<>();

    public Cell(int id) {
        this.id = id;
        value = null;
        showValue = true;
    }

    public Cell(Cell other) {
        this.id = other.id;
        this.value = other.value;
        this.showValue = other.showValue;
        this.isInside = other.isInside; // Assuming MyBoolean is immutable

        // Deep copy of the boarders map
        this.boarders = new HashMap<>();
        for (Location loc : other.boarders.keySet()) {
            Boarder originalBoarder = other.boarders.get(loc);
            this.boarders.put(loc, new Boarder(originalBoarder)); // Assuming Boarder has a copy constructor
        }
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
