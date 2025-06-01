package ch;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Boarder implements Serializable {
    MyBoolean state = MyBoolean.NULL;
    MyBoolean result = MyBoolean.NULL;
    Integer id;
    ArrayList<Pair<Integer, Integer>> cellIds = new ArrayList<>(2);
    ArrayList<Node> connectedNodes = new ArrayList<>(2);

    public Boarder(Integer id) {
        this.id = id;
    }

    public Boarder(Boarder other) {
        this.id = other.id;
        this.state = other.state;
        this.result = other.result;
        this.cellIds = new ArrayList<>(other.cellIds);
    }

    public void toggleBoarder() {
        if (state == MyBoolean.FALSE) {
            state = MyBoolean.NULL;
        }
        else if (state == MyBoolean.TRUE) {
            state = MyBoolean.FALSE;
        }
        else if (state == MyBoolean.NULL) {
            state = MyBoolean.TRUE;
        }
    }

    public void addCellId(int row, int col) {
        cellIds.add(new Pair<>(row, col));

    }

    public ArrayList<Pair<Integer, Integer>> getCellIds() {
        return cellIds;
    }

    public Pair<Integer, Integer> getOtherCellId(int row, int col) {
        var id = new Pair<>(row, col);
        for (Pair<Integer, Integer> cellId : cellIds) {
            if (!cellId.equals(id)) {
                return cellId;
            }
        }
        return null;
    }

    public boolean isCorrect(){
        if (result == MyBoolean.TRUE && state != MyBoolean.TRUE) {
            return false;
        }
        if (state == MyBoolean.TRUE && result != MyBoolean.TRUE) {
            return false;
        }
        return true;
    }

    public MyBoolean getResult() {
        return result;
    }

    public void setResult(MyBoolean result) {
        this.result = result;
    }

    public MyBoolean getState() {
        return state;
    }

    public void setState(MyBoolean state) {
        this.state = state;
    }
}
