package ch;

import java.util.LinkedHashSet;

public class Node {
    public final int row;
    public final int col;
    public LinkedHashSet<Boarder> connectedBoarders;
    public LinkedHashSet<Boarder> activeBoarders;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
        connectedBoarders = new LinkedHashSet<>();
        activeBoarders = new LinkedHashSet<>();
    }

    public Node(Node node) {
        this.row = node.row;
        this.col = node.col;
        this.connectedBoarders = node.connectedBoarders;
        this.activeBoarders = node.activeBoarders;
    }
}
