package ch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Border implements Serializable {
    MyBoolean state = MyBoolean.NULL;
    MyBoolean result = MyBoolean.NULL;
    Integer id;
    LinkedHashSet<Cell> connectedCells = new LinkedHashSet<>(2);
    ArrayList<Node> connectedNodes = new ArrayList<>(2);

    public Border(Integer id) {
        this.id = id;
    }

    public Border(Border other) {
        this.id = other.id;
        this.state = other.state;
        this.result = other.result;
        this.connectedCells = new LinkedHashSet<>();
        this.connectedNodes = new ArrayList<>();
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

    public void addConnectedCell(Cell cell) {
        connectedCells.add(cell);

    }

    public LinkedHashSet<Cell> getConnectedCells() {
        return connectedCells;
    }

    public Cell getOtherCell(Cell otherCell) {
        for (Cell connectedCell : connectedCells) {
            if (!connectedCell.equals(otherCell)) {
                return connectedCell;
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

    public void addConnectedNode(Node node) {
        connectedNodes.add(node);
    }

    public ArrayList<Node> getConnectedNodes() {
        return connectedNodes;
    }

    public void setConnectedNodes(ArrayList<Node> connectedNodes) {
        this.connectedNodes = connectedNodes;
    }
}
