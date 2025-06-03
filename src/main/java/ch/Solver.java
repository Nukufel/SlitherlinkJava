package ch;

import java.util.ArrayList;
import java.util.HashSet;

public class Solver {
    private Grid grid;
    private Grid originalGrid;
    ArrayList<Border> setBorders;
    ArrayList<Border> unsetBorders;
    ArrayList<Node> nodesWithOneBorder;
    ArrayList<Node> cornerNodes;

    public Solver(Grid grid, Grid originalGrid)  {
        this.grid = grid;
        this.originalGrid = originalGrid;
        setBorders = (ArrayList<Border>) grid.getBorders().stream().filter(x -> x.isSet()).toList();
        unsetBorders = (ArrayList<Border>) grid.getBorders().stream().filter(x -> !x.isSet()).toList();
        nodesWithOneBorder = getNodesWithOneBorder();
        cornerNodes = getCornerNodes();
    }

    public boolean hasSingleSolution() {
        //scout patterns
        return !hasSecondSolution();
    }

    public boolean hasSecondSolution(){
        if (nodesWithOneBorder.isEmpty()) {
            if (isOriginalSolution()){
                return false;
            }
            return true;
        }

        // this requires patterns to be found first
        Node node = nodesWithOneBorder.getFirst(); //get a good node
        nodesWithOneBorder.remove(node);

        for (Border border : node.getNullBorders()) { //maybe only get null borders
            if (cellsHaveSpace(border) && nodesHaveSpace(border)){
                border.setState(MyBoolean.TRUE);
                if (hasSecondSolution()){
                    return true;
                }
                border.setState(MyBoolean.NONE);
            }
        }

        nodesWithOneBorder.add(node);
        return false;
    }


    public boolean isOriginalSolution(){
        return grid.isSolved();
    }

    public boolean nodesHaveSpace(Border border){
        for (Node node : border.getConnectedNodes()) {
            if (node.isFull()){
                return false;
            }
        }
        return true;
    }

    public boolean cellsHaveSpace(Border border){
        for (Cell cell : border.getConnectedCells()) {
            if (cell.isFull()){
                return false;
            }
        }
        return true;
    }

    public ArrayList<Node> getNodesWithOneBorder() {
        ArrayList<Node> nodesWithOneBorder = new ArrayList<>();
        for (Node node : grid.getFlattenedNodes()) {
            if (node.activeBorders.size() == 1) {
                nodesWithOneBorder.add(node);
            }
        }
        return nodesWithOneBorder;
    }

    public ArrayList<Border> scoutPatterns() {
        HashSet<Border> scoutedBorders = new HashSet<>();
        int lastSize = 0;
        boolean firstIt = true;
        while(scoutedBorders.size() > lastSize || firstIt){
            firstIt = false;
            lastSize = scoutedBorders.size();

        }
        return null;

    }

    public void cornerPatterns(HashSet<Border> scoutedBorders){
        for (Node node : cornerNodes){
            continue;
        }
    }

    public ArrayList<Node> getCornerNodes() {
        ArrayList<Node> cornerNodes = new ArrayList<>();
        for (Node node : grid.getFlattenedNodes()) {
            if (node.getConnectedBorders().size() == 2) {
                cornerNodes.add(node);
            }
        }
        return cornerNodes;
    }
}
