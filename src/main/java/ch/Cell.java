package ch;

import java.util.ArrayList;
import java.util.HashMap;

public class Cell {
    private int row;
    private int col;
    private int id;
    private Integer value;
    private Boolean showValue;

    private HashMap<String, Boarder> boarders = new HashMap<>();
    private HashMap<String, Boolean> result = new HashMap<>();

    public Cell(int row, int col, ArrayList<Boarder> boarders) {
        this.row = row;
        this.col = col;
        id = row + col;
        value = null;
        showValue = true;

        this.boarders.put("top", boarders.get(0));
        this.boarders.put("right", boarders.get(1));
        this.boarders.put("bottom", boarders.get(2));
        this.boarders.put("left", boarders.get(3));

        result.put("top", null);
        result.put("right", null);
        result.put("bottom", null);
        result.put("left", null);
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

    public HashMap<String, Boolean> getResult() {
        return result;
    }

    public void setResult(HashMap<String, Boolean> result) {
        this.result = result;
    }

    public HashMap<String, Boarder> getBoarders() {
        return boarders;
    }

    public void setBoarders(HashMap<String, Boarder> boarders) {
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

    public void calcValue(){
        var count = 0;
        for (var boarder : boarders.values()) {
            if (boarder.getState() == MyBoolean.TRUE) {
                count++;
            }
        }
        value = count;
    }

    public boolean isCorrect(){
        var setBoarders = boarders.entrySet().stream().filter(e -> e.getValue());
        var correctBoarders = result.entrySet().stream().filter(e -> e.getValue());
        return setBoarders.equals(correctBoarders);
    }

    public void toggleBoarder(String boarder, Boolean value){
        if (value == null){
            var boarderValue = boarders.get(boarder);
            if (boarderValue == null){
                boarders.put(boarder, true);
            }else if (boarderValue){
                boarders.put(boarder, false);
            } else {
                boarders.put(boarder, null);
            }
        }else {
            boarders.put(boarder, value);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Cell cloned = (Cell) super.clone();
        cloned.boarders = new HashMap<>(this.boarders);
        cloned.result = new HashMap<>(this.result);
        return cloned;
    }
}
