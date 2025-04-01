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

    private boolean isOriginalSolution() {
        for (Cell cell : grid.getCells()) {
            if (cell.getInside() != originalGrid.getCells().get(cell.getId()).getInside()) return false;
        }
        return true;
    }

    private boolean isPossibleSolution(Cell cell) {
        ArrayList<Cell> adjacentCellAndThisCell = grid.getAdjacentCells(cell, Settings.directions);
        adjacentCellAndThisCell.add(cell);

        for (Cell adjecentCell : adjacentCellAndThisCell){
             ArrayList<Integer> counts = countAdjacentInAndOutsideCells(adjecentCell);
             int outsideCount = counts.get(0);
             int insideCount = counts.get(1);

             if (!cell.getInside()) {
                 if (cell.getValue() == 3 && outsideCount > 1) return false;
                 if (cell.getValue() == 1 && insideCount > 1) return false;
                 if (cell.getValue() == 0 && insideCount > 0) return false;
             } else if (cell.getInside()) {
                 if (cell.getValue() == 3 && insideCount > 1) return false;
                 if (cell.getValue() == 1 && outsideCount > 1) return false;
                 if (cell.getValue() == 0 && outsideCount > 0) return false;
             } else {
                 if ((cell.getValue() == 1 || cell.getValue() == 3) && ((outsideCount > 1 && insideCount > 1) || outsideCount > 3 || insideCount > 3)) return false;
                 if (cell.getValue() == 0 && outsideCount > 0 && insideCount > 0) return false; // this is not logical
             }

             if (cell.getValue() == 2 && (outsideCount > 2 || insideCount > 2)) return false;
        }
        return true;
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
        boolean changed2 = false;
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
            changed = colorAdjacentCells(cell, cell.getInside());
            for (Cell adjacentCell : adjacentCells) { //todo move to 3 patterns
                if (adjacentCell.getValue() == 3) {
                    changed2 = colorAdjacentCells(adjacentCell, !adjacentCell.getInside());
                }
            }
        }
        return changed || changed2;
    }

    private boolean scout1Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId()) && cell.getInside() == null) {
            changed = true;
            cell.setInside(false);
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() == 3) {
                    adjacentCell.setInside(true);
                }
            }
        }

        if (cell.getInside() == null) {
            ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
            int outsideCount = counts.get(0);
            int insideCount = counts.get(1);
            if (outsideCount > 1) {
                cell.setInside(false);
                changed = true;
            }
            if (insideCount > 1) {
                cell.setInside(true);
                changed = true;
            }
        }

        if (Settings.edgeIDs.contains(cell.getId()) && cell.getInside() != null) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() == 1 && Settings.edgeIDs.contains(adjacentCell.getId()) && adjacentCell.getInside() == null) {
                    adjacentCell.setInside(cell.getInside());
                    changed = true;
                }
            }
        }

        return changed;
    }

    private boolean scout2Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId())) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() == 1) {
                    adjacentCell.setInside(true);
                    cell.setInside(true);
                    changed = true;
                    break;
                }
            }

            for (Cell diagonalCell : getDiagonalCells(cell)) {
                if (diagonalCell.getValue() == 3 && (diagonalCell.getInside() == null || cell.getInside() == null)) {
                    cell.setInside(true);
                    diagonalCell.setInside(false);
                    changed = true;
                    break;
                }
            }
        }

        return changed;
    }


    private boolean scout3Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId()) && cell.getInside() == null) {
           cell.setInside(true);
           changed = true;
        }

        if (Settings.edgeIDs.contains(cell.getId()) && cell.getInside() == null) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() == 1 && Settings.edgeIDs.contains(adjacentCell.getId()) && adjacentCell.getInside() == null) {
                    cell.setInside(true);
                    changed = true;
                    break;
                }
            }
        }

        if (cell.getInside() == null) {
            ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
            int outsideCount = counts.get(0);
            int insideCount = counts.get(1);
            if (outsideCount > 1) {
                cell.setInside(true);
                changed = true;
            }
            if (insideCount > 1) {
                cell.setInside(false);
                changed = true;
            }
        }

        if (cell.getInside() != null) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() == 3 && adjacentCell.getInside() == null) {
                    adjacentCell.setInside(!cell.getInside());
                    changed = true;
                }
            }
        }

        return changed;
    }

    private boolean colorAdjacentCells(Cell cell, boolean value) {
        boolean changed = false;
        for (Cell adjacentCell : grid.getAdjacentCells(cell, Settings.directions)){
            if (adjacentCell.getInside() == null) {
                adjacentCell.setInside(value);
                changed = true;
            }
        }
        return changed;
    }

    private ArrayList<Cell> getDiagonalCells(Cell cell) {
        ArrayList<Cell> diagonalCells  = new ArrayList<>();
        if (cell.getId() == 0) {
            diagonalCells.add(grid.getCells().get(Settings.gridCols + 1));
        } else if (cell.getId() == Settings.gridCols - 1) {
            diagonalCells.add(grid.getCells().get(cell.getId() + Settings.gridCols - 1));
        } else if (cell.getId() == Settings.cellCount - Settings.gridCols) {
            diagonalCells.add(grid.getCells().get(cell.getId() - Settings.gridCols + 1));
        } else if (cell.getId() == Settings.cellCount - 1) {
            diagonalCells.add(grid.getCells().get(cell.getId() - Settings.gridCols - 1));
        }
        return diagonalCells;
    }



}
