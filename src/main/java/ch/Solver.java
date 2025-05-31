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
        grid.setCellsUnidentified();
        while (scoutPatterns()) {
            continue;
        }

        ArrayList<Cell> unidentifiedCells = grid.getUnidentifiedCells();

        boolean hasSecondSolution = hasSecondSolution(unidentifiedCells);
        return !hasSecondSolution;
    }

    private boolean hasSecondSolution(ArrayList<Cell> unidentifiedCells) {
        MyBoolean[] statesToCheck = {MyBoolean.TRUE, MyBoolean.FALSE};
        if (unidentifiedCells.isEmpty()) {
            return !isOriginalSolution() && isGridValid();
        }

        Cell cell = unidentifiedCells.getFirst();
        ArrayList<Cell> remainingCells = new ArrayList<>(unidentifiedCells.subList(1, unidentifiedCells.size()));

        for (MyBoolean value : statesToCheck) {
            cell.setIsInside(value);
            if (isPossibleSolution(cell)){
                if (hasSecondSolution(remainingCells)) {
                    return true;
                }
            }
        }

        cell.setIsInside(MyBoolean.NULL);
        return false;
    }

    private boolean isOriginalSolution() {
        for (Cell cell : grid.getCells()) {
            for (Cell originalCell : originalGrid.getCells()){
                if (cell.getId() == originalCell.getId()){
                    if (!cell.getIsInside().equals(originalCell.getIsInside())){
                        return false;
                    }
                    break;
                }
            }
        }
        return true;
    }

    private boolean isGridValid() {
        for (Cell cell : grid.getCells()) {
            if (!isPossibleSolution(cell)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPossibleSolution(Cell cell) {
        ArrayList<Cell> adjacentCellAndThisCell = grid.getAdjacentCells(cell);
        adjacentCellAndThisCell.add(cell);


        for (Cell adjecentCell : adjacentCellAndThisCell){
             ArrayList<Integer> counts = countAdjacentInAndOutsideCells(adjecentCell);
             int outsideCount = counts.get(0);
             int insideCount = counts.get(1);

             if (adjecentCell.getIsInside() == MyBoolean.FALSE) {
                 if (adjecentCell.getValue() != null) {
                     if (adjecentCell.getValue() == 3 && outsideCount > 1) return false;
                     if (adjecentCell.getValue() == 1 && insideCount > 1) return false;
                     if (adjecentCell.getValue() == 0 && insideCount > 0) return false;
                 }
                 if (insideCount >= 4) return false;
             } else if (adjecentCell.getIsInside() == MyBoolean.TRUE) {
                 if (adjecentCell.getValue() != null) {
                     if (adjecentCell.getValue() == 3 && insideCount > 1) return false;
                     if (adjecentCell.getValue() == 1 && outsideCount > 1) return false;
                     if (adjecentCell.getValue() == 0 && outsideCount > 0) return false;
                 }
                 if (outsideCount >= 4) return false;
             } else {
                 if (adjecentCell.getValue() != null) {
                     if ((adjecentCell.getValue() == 1 || adjecentCell.getValue() == 3) &&
                             ((outsideCount > 1 && insideCount > 1) || outsideCount > 3 || insideCount > 3))
                         return false;
                     if (adjecentCell.getValue() == 0 && outsideCount > 0 && insideCount > 0) return false;
                 }
             }
            if (adjecentCell.getValue() != null) {
                if (adjecentCell.getValue() == 2 && (outsideCount > 2 || insideCount > 2)) return false;
            }
        }
        return true;
    }

    public ArrayList<Integer> countAdjacentInAndOutsideCells(Cell cell) {
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(cell);
        int outsideCount = 4 - adjacentCells.size();
        int insideCount = 0;

        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getIsInside() != MyBoolean.NULL) {
                if (adjacentCell.getIsInside() == MyBoolean.TRUE) {
                    insideCount++;
                } else if (adjacentCell.getIsInside() == MyBoolean.FALSE) {
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
            ArrayList<Cell> adjacentCells = grid.getAdjacentCells(cell);

            if (cell.getValue() == null) {
                changed = scoutNullPatterns(cell, adjacentCells);
            }else if (cell.getValue() == 0) {
                changed = scout0Patterns(cell, adjacentCells);
            }else if (cell.getValue() == 1) {
                changed = scout1Patterns(cell, adjacentCells);
            }else if (cell.getValue() == 2) {
                changed = scout2Patterns(cell, adjacentCells);
            }else if (cell.getValue() == 3) {
                changed = scout3Patterns(cell, adjacentCells);
            }
        }
        return changed;
    }

    private boolean scoutNullPatterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        if (cell.getIsInside() == MyBoolean.NULL) {
            ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
            int outsideCount = counts.get(0);
            int insideCount = counts.get(1);

            if (Settings.cornerIDs.contains(cell.getId())){
                if (outsideCount - adjacentCells.size() >= 2){
                    cell.setIsInside(MyBoolean.FALSE);
                    changed = true;
                }
            }

            if (Settings.edgeIDs.contains(cell.getId())){
                if (outsideCount - adjacentCells.size() >= 3){
                    cell.setIsInside(MyBoolean.FALSE);
                    changed = true;
                }
            }



            if (outsideCount == 4) {
                cell.setIsInside(MyBoolean.FALSE);
                changed = true;
            } else if (insideCount == 4) {
                cell.setIsInside(MyBoolean.TRUE);
                changed = true;
            }
        }
        return changed;
    }

    private boolean scout0Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        if (cell.getIsInside() == MyBoolean.NULL && Settings.edgeIDs.contains(cell.getId())) {
           cell.setIsInside(MyBoolean.FALSE);
           changed = true;
        }

        if (cell.getIsInside() == MyBoolean.NULL) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getIsInside() != MyBoolean.NULL) {
                    cell.setIsInside(adjacentCell.getIsInside());
                    changed = true;
                    break;
                }
            }
        }

        if (cell.getIsInside() != MyBoolean.NULL) {
            changed = colorAdjacentCells(cell, cell.getIsInside());
        }
        return changed;
    }

    private boolean scout1Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId()) && cell.getIsInside() == MyBoolean.NULL) {
            changed = true;
            cell.setIsInside(MyBoolean.FALSE);
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() != null && adjacentCell.getValue() == 3) {
                    adjacentCell.setIsInside(MyBoolean.TRUE);
                }
            }
        }

        if (cell.getIsInside() == MyBoolean.NULL) {
            ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
            int outsideCount = counts.get(0);
            int insideCount = counts.get(1);
            if (outsideCount > 1) {
                cell.setIsInside(MyBoolean.FALSE);
                changed = true;
            }
            if (insideCount > 1) {
                cell.setIsInside(MyBoolean.TRUE);
                changed = true;
            }
        }

        if (Settings.edgeIDs.contains(cell.getId()) && cell.getIsInside() != MyBoolean.NULL) {
            for (Cell adjacentCell : adjacentCells) {
                if (cell.getIsInside() == MyBoolean.TRUE && adjacentCell.getIsInside() == MyBoolean.NULL) {
                    adjacentCell.setIsInside(MyBoolean.TRUE);
                    changed = true;
                }
                if (adjacentCell.getValue() != null && adjacentCell.getValue() == 1 && Settings.edgeIDs.contains(adjacentCell.getId())) {
                    if (cell.getIsInside() == MyBoolean.NULL || adjacentCell.getIsInside() == MyBoolean.NULL) {
                        if (cell.getIsInside() == MyBoolean.FALSE) {
                            adjacentCell.setIsInside(MyBoolean.FALSE);
                            changed = true;
                        } else if (adjacentCell.getIsInside() == MyBoolean.FALSE) {
                            cell.setIsInside(MyBoolean.FALSE);
                            changed = true;
                        }
                    }
                }
            }
        }

        return changed;
    }

    private boolean scout2Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId()) ) {
            if (cell.getIsInside() != MyBoolean.NULL) {
                colorAdjacentCells(cell, MyBoolean.TRUE);
            }

            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getIsInside() == MyBoolean.NULL || cell.getIsInside() == MyBoolean.NULL) {
                    if (adjacentCell.getValue() != null && adjacentCell.getValue() == 1) {
                        adjacentCell.setIsInside(MyBoolean.TRUE);
                        cell.setIsInside(MyBoolean.TRUE);
                        changed = true;
                        break;
                    }
                }
            }

            for (Cell diagonalCell : getDiagonalCells(cell)) {
                if (diagonalCell.getIsInside() == MyBoolean.NULL || cell.getIsInside() == MyBoolean.NULL) {
                    if (diagonalCell.getValue() != null && diagonalCell.getValue() == 3) {
                        cell.setIsInside(MyBoolean.TRUE);
                        diagonalCell.setIsInside(MyBoolean.FALSE);
                        changed = true;
                        break;
                    }
                }

                if (cell.getIsInside() == MyBoolean.FALSE) {
                    diagonalCell.setIsInside(MyBoolean.TRUE);
                }
            }
        }

        return changed;
    }


    private boolean scout3Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId()) && cell.getIsInside() == MyBoolean.NULL) {
           cell.setIsInside(MyBoolean.TRUE);
           changed = true;
        }

        if (Settings.edgeIDs.contains(cell.getId()) && cell.getIsInside() == MyBoolean.NULL) {
            int inside2s = 0;
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() != null && adjacentCell.getValue() == 1 && Settings.edgeIDs.contains(adjacentCell.getId()) && adjacentCell.getIsInside() == MyBoolean.NULL) {
                    cell.setIsInside(MyBoolean.TRUE);
                    changed = true;
                    break;
                }

                if (Settings.edgeIDs.contains(adjacentCell.getId()) && adjacentCell.getIsInside() == MyBoolean.TRUE &&
                        adjacentCell.getValue() != null && adjacentCell.getValue() == 2) {
                    inside2s++;
                }

                if (inside2s == 2){
                    if (outsideEdge3Pattern(cell, adjacentCells)) {
                        changed = true;
                    }
                }
            }
        }

        if (cell.getIsInside() == MyBoolean.NULL) {
            ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
            int outsideCount = counts.get(0);
            int insideCount = counts.get(1);
            if (outsideCount > 1) {
                cell.setIsInside(MyBoolean.TRUE);
                changed = true;
            }
            if (insideCount > 1) {
                cell.setIsInside(MyBoolean.FALSE);
                changed = true;
            }
        }

        if (cell.getIsInside() != MyBoolean.NULL) {
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() != null && adjacentCell.getValue() == 3 && adjacentCell.getIsInside() == MyBoolean.NULL) {
                    adjacentCell.setIsInside(MyBoolean.switchMyBool(cell.getIsInside()));
                    changed = true;
                }
            }
        }

        return changed;
    }

    private boolean outsideEdge3Pattern(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;
        for (Cell adjacentCell2 : adjacentCells) {
            if (adjacentCell2.getIsInside() == MyBoolean.NULL) {
                adjacentCell2.setIsInside(MyBoolean.TRUE);
                changed = true;
            }
        }
        for (Cell diagonalCell : getDiagonalCells(cell)) {
            if (diagonalCell.getIsInside() == MyBoolean.NULL) {
                diagonalCell.setIsInside(MyBoolean.TRUE);
                changed = true;
            }
        }
        return changed;
    }

    private boolean colorAdjacentCells(Cell cell, MyBoolean value) {
        boolean changed = false;
        for (Cell adjacentCell : grid.getAdjacentCells(cell)){
            if (adjacentCell.getIsInside() == MyBoolean.NULL) {
                adjacentCell.setIsInside(value);
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
