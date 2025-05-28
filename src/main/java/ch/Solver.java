package ch;

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
        ArrayList<Cell> unidentifiedCells = grid.getCopyOfCells();
        return !hasSecondSolution(unidentifiedCells);
    }

    private boolean hasSecondSolution(ArrayList<Cell> unidentifiedCells) {
        MyBoolean[] statesToCheck = {MyBoolean.TRUE, MyBoolean.FALSE};
        if (unidentifiedCells.isEmpty()) {
            return true;
        }

        Cell cell = unidentifiedCells.removeFirst();

        for (MyBoolean value : statesToCheck) {
            cell.setIsInside(value);
            if (isPossibleSolution(cell) && !isOriginalSolution()){
                if (hasSecondSolution(unidentifiedCells)) {
                    return true;
                }
            }
        }

        cell.setIsInside(MyBoolean.NULL);
        unidentifiedCells.add(cell);
        return false;
    }

    private boolean isOriginalSolution() {
        for (Cell cell : grid.getCells()) {
            if (cell.getIsInside() != originalGrid.getCells().get(cell.getId()).getIsInside()) return false;
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

             if (adjecentCell.getValue() != null) {
                 if (adjecentCell.getIsInside() == MyBoolean.FALSE) {
                     if (adjecentCell.getValue() == 3 && outsideCount > 1) return false;
                     if (adjecentCell.getValue() == 1 && insideCount > 1) return false;
                     if (adjecentCell.getValue() == 0 && insideCount > 0) return false;
                 } else if (adjecentCell.getIsInside() == MyBoolean.TRUE) {
                     if (adjecentCell.getValue() == 3 && insideCount > 1) return false;
                     if (adjecentCell.getValue() == 1 && outsideCount > 1) return false;
                     if (adjecentCell.getValue() == 0 && outsideCount > 0) return false;
                 } else {
                     if ((adjecentCell.getValue() == 1 || adjecentCell.getValue() == 3) && ((outsideCount > 1 && insideCount > 1) || outsideCount > 3 || insideCount > 3))
                         return false;
                     if (adjecentCell.getValue() == 0 && outsideCount > 0 && insideCount > 0) return false;
                 }

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
                changed = scoutNullPatterns(cell);
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

    private boolean scoutNullPatterns(Cell cell) {
        boolean changed = false;
        ArrayList<Integer> counts = countAdjacentInAndOutsideCells(cell);
        int outsideCount = counts.get(0);
        int insideCount = counts.get(1);
        if (outsideCount == 4) {
            cell.setIsInside(MyBoolean.FALSE);
            changed = true;
        }
        if (insideCount == 4) {
            cell.setIsInside(MyBoolean.TRUE);
            changed = true;
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
                if (adjacentCell.getValue() != null && adjacentCell.getValue() == 1 && Settings.edgeIDs.contains(adjacentCell.getId()) && adjacentCell.getIsInside() == MyBoolean.NULL) {
                    adjacentCell.setIsInside(cell.getIsInside());
                    changed = true;
                }
            }
        }

        return changed;
    }

    private boolean scout2Patterns(Cell cell, ArrayList<Cell> adjacentCells) {
        boolean changed = false;

        if (Settings.cornerIDs.contains(cell.getId()) ) {
            //TODO in some cases always true (both)
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
                        diagonalCell.setIsInside(MyBoolean.TRUE);
                        changed = true;
                        break;
                    }
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
            for (Cell adjacentCell : adjacentCells) {
                if (adjacentCell.getValue() != null && adjacentCell.getValue() == 1 && Settings.edgeIDs.contains(adjacentCell.getId()) && adjacentCell.getIsInside() == MyBoolean.NULL) {
                    cell.setIsInside(MyBoolean.TRUE);
                    changed = true;
                    break;
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

        /**
         * for (Cell adjacentCell : adjacentCells) {
         *                 if (adjacentCell.getValue() == 3) {
         *                     changed2 = colorAdjacentCells(adjacentCell, MyBoolean.switchMyBool(adjacentCell.getIsInside()));
         *                 }
         *             }
         */

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
