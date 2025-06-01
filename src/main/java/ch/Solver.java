package ch;

import java.util.ArrayList;

public class Solver {
    private static final boolean[] POSSIBLE_VALUES = {true, false};
    private Grid grid;
    private Grid originalGrid;

    public Solver(Grid grid, Grid originalGrid)  {
        this.grid = grid;
        this.originalGrid = originalGrid;

    }

    public boolean hasSingleSolution() {
        return true;
    }



}
