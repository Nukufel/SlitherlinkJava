package ch;

import java.util.ArrayList;

public class Node {
    public final int row;
    public final int col;
    public ArrayList<Boarder> connectedBoarders;
    public ArrayList<Boarder> activeBoarders;

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
        connectedBoarders = new ArrayList<>();
    }
}
