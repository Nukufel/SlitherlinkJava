package ch;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Solver {
    private static final boolean[] POSSIBLE_VALUES = {true, false};
    private Grid grid;
    private Grid originalGrid;

    public Solver(Grid grid, Grid originalGrid) {
        this.grid = grid;
        this.originalGrid = originalGrid;
    }

    public boolean hasSingleSolution() {
        grid.setCellsUnidentified();
        while (scoutPatterns()) {
            continue;
        }
        ArrayList<Cell> cells = grid.getCells();
        return !solve(cells);
    }

    private boolean solve(ArrayList<Cell> cells) {
        Cell cell = null;
        try {
            cell = cells.getLast();
        } catch (IndexOutOfBoundsException e) {
            return true;
        }

        cells.remove(cell);

        for (boolean value : POSSIBLE_VALUES) {
            cell.setInside(value);
            if (isPossibleSolution(cell) && !isOriginalSolution()) {
                if (solve(cells)) {
                    return true;
                }
            }
        }

        cell.setInside(null);
        cells.add(cell);
        return false;
    }

    public ArrayList<Integer> countAdjacentInAndOutsideCells(Cell cell) {
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(cell, Settings.directions);
        int outsideCount = 4 - adjacentCells.size();
        int insideCount = 0;

        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getInside() != null) {
                if (adjacentCell.getInside()) {
                    insideCount++;
                } else if (!adjacentCell.getInside()) {
                    outsideCount++;
                }
            }
        }

        ArrayList<Integer> counts = new ArrayList<>();
        counts.add(outsideCount);
        counts.add(insideCount);
        return counts;
    }

    private boolean scoutPatterns() {
        boolean changed = false;
        for (Cell cell : grid.getCells()) {
            ArrayList<Cell> adjacentCells = grid.getAdjacentCells(cell, Settings.directions);
            if (cell.getValue() == null) {
                changed = scoutNullPatterns(cell);
            }
            if (cell.getValue() == 0) {
                changed = scout0Patterns(cell, adjacentCells);
            }
            if (cell.getValue() == 1) {
                changed = scout1Patterns(cell, adjacentCells);
            }
            if (cell.getValue() == 2) {
                changed = scout2Patterns(cell, adjacentCells);
            }
            if (cell.getValue() == 3) {
                changed = scout3Patterns(cell, adjacentCells);
            }
        }
        return changed;
    }

    private boolean scoutNullPatterns(Cell cell) {
        boolean changed = false;
        ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
        int outsideCount = counts.get(0);
        int insideCount = counts.get(1);
        if (outsideCount == 4) {
            cell.setInside(false);
            changed = true;
        }
        if (insideCount == 4) {
            cell.setInside(true);
            changed = true;
        }
        return changed;
    }

    private boolean scout0Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        if (cell.getInside() == null && Settings.edgeIDs.contains(cell.getId())) {
           cell.setInside(false);
           changed = true;
        }

        if (cell.getInside() == null) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getInside() != null) {
                    cell.setInside(adjacentCell.getInside());
                    changed = true;
                    break;
                }
            }
        }

        if (cell.getInside() != null) {

        }
        return changed;
    }

    private boolean scout1Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        return changed;
    }

    private boolean scout2Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        return changed;
    }

    private boolean scout3Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        return changed;
    }


}
