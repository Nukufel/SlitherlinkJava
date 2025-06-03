package ch;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Node {
    public final int row;
    public final int col;
    public LinkedHashSet<Border> connectedBorders = new LinkedHashSet<>();
    public LinkedHashSet<Border> activeBorders = new LinkedHashSet<>();

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Node(Node node) {
        this.row = node.row;
        this.col = node.col;
        this.connectedBorders = new LinkedHashSet<>();
        this.activeBorders = new LinkedHashSet<>();
    }

    public LinkedHashSet<Border> getActiveBorders() {
        return activeBorders;
    }

    public void setActiveBorders(LinkedHashSet<Border> activeBorders) {
        this.activeBorders = activeBorders;
    }

    public ArrayList<Border> getInactiveBorders() {
        var inactiveBorders = new ArrayList<Border>();
        for (Border b : connectedBorders) {
            if (!activeBorders.contains(b)) {
                inactiveBorders.add(b);
            }
        }
        return inactiveBorders;
    }

    public boolean isFull(){
        return activeBorders.size() >= 2;
    }
}
