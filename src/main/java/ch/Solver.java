package ch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solver {
    private Grid grid;
    private Grid originalGrid;
    ArrayList<Cell> cornerCells;
    ArrayList<Cell> patternCells;

    public Solver(Grid grid, Grid originalGrid)  {
        this.grid = grid;
        this.originalGrid = originalGrid;
        grid.setCellsUnidentified();
        cornerCells = getCornerCells();
        patternCells = new ArrayList<>();
    }

    public boolean hasSingleSolution() {
        scoutPatterns();
        boolean solve = solve();
        System.out.println(solve);
        return !solve;
    }

    public boolean solve() {

        Cell unidentifiedCell = getUnidentifiedCell();
        if (unidentifiedCell == null) {
            if (isOriginalSolution()){
                return false;
            }
             return true;
        }

        for (MyBoolean state : MyBoolean.validStates()){
            unidentifiedCell.setState(state);
            if (isPossibleSolution()){
                if (solve()){
                    return true;
                }
            }
        }

        unidentifiedCell.setState(MyBoolean.NONE);
        return false;
    }

    public boolean isPossibleSolution() {
        for (Cell cell : grid.getFlattenedCells()){
            if (!isCellStateValid(cell)){
                return false;
            }
        }
        //test if none are seperated
        return true;
    }

    public void scoutPatterns(){
        for (Cell cell : grid.getFlattenedCells()) {
            if (cell.hasValue()){
                if (cell.getValue() == 0){
                    zeroPatterns(cell);
                } else if (cell.getValue() == 1){
                    onePatterns(cell);
                } else if (cell.getValue() == 2){

                } else {
                    threePatterns(cell);
                }
            }
        }
    }

    public void zeroPatterns(Cell cell){
        cornerPattern(cell, MyBoolean.FALSE);
    }

    public void onePatterns(Cell cell){
        cornerPattern(cell, MyBoolean.FALSE);
    }

    public void towPatterns(Cell cell){

    }

    public void threePatterns(Cell cell){
        cornerPattern(cell, MyBoolean.TRUE);
    }

    public void setStateForCell(Cell cell, MyBoolean state){
        cell.setState(state);
        patternCells.add(cell);
    }

    public void cornerPattern(Cell cell, MyBoolean state){
        if (cornerCells.contains(cell)) {
            setStateForCell(cell, state);
        }
    }

    public ArrayList<Cell> getCornerCells() {
        ArrayList <Cell> cornerCells = new ArrayList<>();
        cornerCells.add(grid.getCells().getFirst().getFirst());
        cornerCells.add(grid.getCells().getFirst().getLast());
        cornerCells.add(grid.getCells().getLast().getFirst());
        cornerCells.add(grid.getCells().getLast().getLast());
        return cornerCells;
    }

    public Cell getUnidentifiedCell(){
        List<Cell> unidentifiedCells = grid.getFlattenedCells().stream().filter(cell -> !cell.hasState()).toList();
        if (unidentifiedCells.isEmpty()) {
            return null;
        }
        return unidentifiedCells.getFirst();
    }

    public int countInsideCells(){
        int count = 0;
        for (Cell cell : grid.getFlattenedCells()) {
            if (cell.hasState()) {
                count++;
            }
        }
        return count;
    }

    public boolean isCellStateValid(Cell cell){
        int insideCount = countInsideAdjacentCells(cell);
        int outsideCount = countOutsideAdjacentCells(cell);

        if (cell.getValue() == null){
            return true;
        }

        int cellValue = cell.getValue();
        if (cell.getState() == MyBoolean.TRUE) {
            if (cellValue == 3 && insideCount > 1){
                return false;
            }
            else if (cellValue == 1 && outsideCount > 1){
                return false;
            }
            else if (cellValue == 0 && outsideCount > 0){
                return false;
            }
        } else if (cell.getState() == MyBoolean.FALSE) {
            if (cellValue == 3 && outsideCount > 1){
                return false;
            }
            else if (cellValue == 1 && insideCount > 1){
                return false;
            }
            else if (cellValue == 0 && insideCount > 0){
                return false;
            }
        } else {
            if ((cellValue == 1 || cellValue == 3) && ((insideCount > 1 && outsideCount > 1) || (insideCount > 3 || outsideCount > 3))){
                return false;
            }
            if (cellValue == 0 && insideCount > 0 && outsideCount > 0) {
                return false;
            }
        }
        if (cellValue == 2 && insideCount > 2 || outsideCount > 2){
            return false;
        }
        return true;
    }

    public int countInsideAdjacentCells(Cell cell){
        int count = 0;
        for (Cell adjacentCell : grid.getAdjacentCells(cell)) {
            if (adjacentCell.getState() == MyBoolean.TRUE) {
                count++;
            }
        }
        return count;
    }

    public int countOutsideAdjacentCells(Cell cell){
        int count = 4;
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(cell);
        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getState() == MyBoolean.FALSE) {
                count++;
            }
        }
        return count - adjacentCells.size();
    }

    public boolean isOriginalSolution(){
        for (int i = 0; i < Settings.gridRows; i++){
            for (int j = 0; j < Settings.gridCols; j++){
                if (grid.getCells().get(i).get(j).getState() != originalGrid.getCells().get(i).get(j).getState()){
                    return false;
                }
            }
        }
        return true;
    }
}
