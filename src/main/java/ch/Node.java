package ch;

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
}
