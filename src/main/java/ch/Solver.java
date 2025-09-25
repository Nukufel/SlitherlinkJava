package ch;

import java.util.ArrayList;
import java.util.HashSet;
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
        solve();
        return true;
    }

    public boolean solve() {

        Cell randomUnidentifiedCell = getRandomUnidentifiedCell();
        if (randomUnidentifiedCell == null) {
            return true;
        }

        for (MyBoolean state : MyBoolean.validStates()){
            randomUnidentifiedCell.setState(state);
            if (true){ //some constraint
                if (solve()){
                    return true;
                }
            }
        }

        randomUnidentifiedCell.setState(MyBoolean.NONE);
        return false;
    }

    public boolean isValidGrid() {

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

    public Cell getRandomUnidentifiedCell(){
        Random rand = new Random();
        List<Cell> unidentifiedCells = grid.getFlattenedCells().stream().filter(cell -> !cell.hasState()).toList();
        if (unidentifiedCells.isEmpty()) {
            return null;
        }
        return unidentifiedCells.get(rand.nextInt(unidentifiedCells.size()));
    }


}
