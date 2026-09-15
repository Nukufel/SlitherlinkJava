package ch;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.IdentityHashMap;

public class Solver {
    private final Grid grid;

    private final ArrayList<Cell> allCells;
    private final IdentityHashMap<Cell, ArrayList<Cell>> neighbors =
            new IdentityHashMap<>();

    private final ArrayList<Cell> cornerCells;
    private final ArrayList<Cell> trail = new ArrayList<>();

    private boolean contradiction = false;

    public Solver(Grid grid) {
        this.grid = grid;

        allCells = grid.getFlattenedCells();
        cornerCells = getCornerCells();

        for (Cell cell : allCells) {
            neighbors.put(
                    cell,
                    grid.getAdjacentCells(cell)
            );
        }
    }

    public boolean hasSingleSolution() {
        grid.setCellsUnidentified();

        trail.clear();
        contradiction = false;

        if (!solve()) {
            return false;
        }

        trail.clear();

        int solutions = countSolutions(2);

        return solutions == 1;
    }

    private int countSolutions(int limit) {
        if (contradiction) {
            return 0;
        }

        Cell unidentifiedCell = getUnidentifiedCell();

        // Complete assignment
        if (unidentifiedCell == null) {
            if (isGridValid()) {
                return 1;
            }

            return 0;
        }

        int solutionCount = 0;

        for (MyBoolean state : MyBoolean.validStates()) {

            int mark = trail.size();

            setStateForCell(unidentifiedCell, state);

            if (!contradiction && solve()) {

                solutionCount += countSolutions(
                        limit - solutionCount
                );
            }

            rollback(mark);

            if (solutionCount >= limit) {
                return solutionCount;
            }
        }

        return solutionCount;
    }

    private boolean solve() {
        boolean changed;

        do {
            changed = scoutPatterns();

            if (contradiction) {
                return false;
            }

        } while (changed);

        return true;
    }

    private boolean isGridValid() {
        for (Cell cell : allCells) {

            if (!cell.hasState()) {
                return false;
            }

            if (cell.hasValue()) {
                int actualEdges = countLoopEdgesAroundCell(cell);

                if (actualEdges != cell.getValue()) {
                    return false;
                }
            }
        }

        return hasSingleLoop();
    }

    private boolean hasSingleLoop() {
        int rows = Settings.gridRows;
        int cols = Settings.gridCols;

        boolean[][] horizontal = new boolean[rows + 1][cols];
        boolean[][] vertical = new boolean[rows][cols + 1];

        // Build loop edges from INSIDE / OUTSIDE differences
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                boolean inside =
                        grid.getCells().get(row).get(col).getState()
                                == MyBoolean.TRUE;

                boolean above =
                        row > 0 &&
                                grid.getCells().get(row - 1).get(col).getState()
                                        == MyBoolean.TRUE;

                boolean below =
                        row < rows - 1 &&
                                grid.getCells().get(row + 1).get(col).getState()
                                        == MyBoolean.TRUE;

                boolean left =
                        col > 0 &&
                                grid.getCells().get(row).get(col - 1).getState()
                                        == MyBoolean.TRUE;

                boolean right =
                        col < cols - 1 &&
                                grid.getCells().get(row).get(col + 1).getState()
                                        == MyBoolean.TRUE;

                horizontal[row][col] = inside != above;
                horizontal[row + 1][col] = inside != below;

                vertical[row][col] = inside != left;
                vertical[row][col + 1] = inside != right;
            }
        }

        int[][] degree = new int[rows + 1][cols + 1];

        int edgeCount = 0;

        // Horizontal edges
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col < cols; col++) {

                if (horizontal[row][col]) {
                    degree[row][col]++;
                    degree[row][col + 1]++;
                    edgeCount++;
                }
            }
        }

        // Vertical edges
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols; col++) {

                if (vertical[row][col]) {
                    degree[row][col]++;
                    degree[row + 1][col]++;
                    edgeCount++;
                }
            }
        }

        if (edgeCount == 0) {
            return false;
        }

        int startRow = -1;
        int startCol = -1;
        int activeVertices = 0;

        // Every loop vertex must have degree exactly 2
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col <= cols; col++) {

                if (degree[row][col] != 0 &&
                        degree[row][col] != 2) {
                    return false;
                }

                if (degree[row][col] == 2) {
                    activeVertices++;

                    if (startRow == -1) {
                        startRow = row;
                        startCol = col;
                    }
                }
            }
        }

        boolean[][] visited =
                new boolean[rows + 1][cols + 1];

        ArrayDeque<int[]> queue = new ArrayDeque<>();

        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int visitedVertices = 0;

        while (!queue.isEmpty()) {

            int[] vertex = queue.removeFirst();

            int row = vertex[0];
            int col = vertex[1];

            visitedVertices++;

            // Left
            if (col > 0 && horizontal[row][col - 1]) {
                addVertex(queue, visited, row, col - 1);
            }

            // Right
            if (col < cols && horizontal[row][col]) {
                addVertex(queue, visited, row, col + 1);
            }

            // Up
            if (row > 0 && vertical[row - 1][col]) {
                addVertex(queue, visited, row - 1, col);
            }

            // Down
            if (row < rows && vertical[row][col]) {
                addVertex(queue, visited, row + 1, col);
            }
        }

        return visitedVertices == activeVertices;
    }

    private void addVertex(
            ArrayDeque<int[]> queue,
            boolean[][] visited,
            int row,
            int col
    ) {
        if (!visited[row][col]) {
            visited[row][col] = true;
            queue.addLast(new int[]{row, col});
        }
    }

    private int countLoopEdgesAroundCell(Cell cell) {
        int edges = 0;

        MyBoolean state = cell.getState();

        ArrayList<Cell> adjacentCells = getNeighbors(cell);

        for (Cell adjacentCell : adjacentCells) {
            if (adjacentCell.getState() != state) {
                edges++;
            }
        }

        if (state == MyBoolean.TRUE) {
            edges += 4 - adjacentCells.size();
        }

        return edges;
    }

    public boolean scoutPatterns() {
        boolean changed = false;

        for (Cell cell : allCells) {

            if (!cell.hasValue()) {
                continue;
            }

            switch (cell.getValue()) {
                case 0 -> changed |= zeroPatterns(cell);
                case 1 -> changed |= onePatterns(cell);
                case 2 -> changed |= twoPatterns(cell);
                case 3 -> changed |= threePatterns(cell);
            }

            changed |= statePatterns(cell);

            if (contradiction) {
                return changed;
            }
        }

        return changed;
    }

    private boolean statePatterns(Cell clueCell) {
        ArrayList<Cell> localCells = new ArrayList<>(5);

        localCells.add(clueCell);
        localCells.addAll(getNeighbors(clueCell));

        int count = localCells.size();
        int combinations = 1 << count;
        int fullMask = combinations - 1;

        int validCount = 0;

        int alwaysTrue = fullMask;

        int alwaysFalse = fullMask;

        int clue = clueCell.getValue();
        int neighborCount = count - 1;

        for (int mask = 0; mask < combinations; mask++) {

            boolean matchesKnownStates = true;

            for (int i = 0; i < count; i++) {
                Cell cell = localCells.get(i);

                if (!cell.hasState()) {
                    continue;
                }

                boolean candidateState =
                        (mask & (1 << i)) != 0;

                boolean actualState =
                        cell.getState() == MyBoolean.TRUE;

                if (candidateState != actualState) {
                    matchesKnownStates = false;
                    break;
                }
            }

            if (!matchesKnownStates) {
                continue;
            }

            boolean centerInside = (mask & 1) != 0;

            int edges = 0;

            if (centerInside) {
                edges += 4 - neighborCount;
            }

            for (int i = 1; i < count; i++) {
                boolean neighborInside =
                        (mask & (1 << i)) != 0;

                if (neighborInside != centerInside) {
                    edges++;
                }
            }

            if (edges != clue) {
                continue;
            }

            validCount++;

            alwaysTrue &= mask;
            alwaysFalse &= (~mask) & fullMask;
        }

        if (validCount == 0) {
            contradiction = true;
            return false;
        }

        boolean changed = false;

        for (int i = 0; i < count; i++) {

            Cell cell = localCells.get(i);

            if (cell.hasState()) {
                continue;
            }

            int bit = 1 << i;

            if ((alwaysTrue & bit) != 0) {
                changed |= setStateForCell(
                        cell,
                        MyBoolean.TRUE
                );
            }
            else if ((alwaysFalse & bit) != 0) {
                changed |= setStateForCell(
                        cell,
                        MyBoolean.FALSE
                );
            }

            if (contradiction) {
                return changed;
            }
        }

        return changed;
    }



    public boolean setStateForCell(Cell cell, MyBoolean state) {
        MyBoolean currentState = cell.getState();

        if (currentState == state) {
            return false;
        }

        if (currentState != MyBoolean.NONE) {
            contradiction = true;
            return false;
        }

        trail.add(cell);
        cell.setState(state);

        if (!isCellStateValid(cell)) {
            contradiction = true;
            return true;
        }

        for (Cell adjacentCell : getNeighbors(cell)) {
            if (!isCellStateValid(adjacentCell)) {
                contradiction = true;
                break;
            }
        }

        return true;
    }

    private void rollback(int mark) {
        while (trail.size() > mark) {
            Cell cell = trail.removeLast();
            cell.setState(MyBoolean.NONE);
        }

        contradiction = false;
    }


    public Cell getUnidentifiedCell() {
        Cell bestCell = null;
        int bestScore = Integer.MIN_VALUE;

        for (Cell cell : allCells) {

            if (cell.hasState()) {
                continue;
            }

            int score = 0;

            if (cell.hasValue()) {
                score += 10;
            }

            for (Cell adjacentCell : getNeighbors(cell)) {

                if (adjacentCell.hasState()) {
                    score += 3;
                }

                if (adjacentCell.hasValue()) {
                    score += 2;
                }
            }

            if (cornerCells.contains(cell)) {
                score += 2;
            }

            if (score > bestScore) {
                bestScore = score;
                bestCell = cell;
            }
        }

        return bestCell;
    }


    public boolean isCellStateValid(Cell cell) {
        if (!cell.hasValue()) {
            return true;
        }

        int clue = cell.getValue();

        if (cell.hasState()) {
            return canReachClue(cell, cell.getState(), clue);
        }

        return canReachClue(cell, MyBoolean.TRUE, clue)
                || canReachClue(cell, MyBoolean.FALSE, clue);
    }

    private boolean canReachClue(Cell cell, MyBoolean assumedState, int clue) {
        ArrayList<Cell> adjacentCells = getNeighbors(cell);

        int guaranteedEdges = 0;
        int unknownSides = 0;

        int exteriorSides = 4 - adjacentCells.size();

        if (assumedState == MyBoolean.TRUE) {
            guaranteedEdges += exteriorSides;
        }

        for (Cell adjacentCell : adjacentCells) {

            if (!adjacentCell.hasState()) {
                unknownSides++;
            } else if (adjacentCell.getState() != assumedState) {
                guaranteedEdges++;
            }
        }

        int maxEdges = guaranteedEdges + unknownSides;

        return clue >= guaranteedEdges && clue <= maxEdges;
    }

    //PATTERNS

    public boolean zeroPatterns(Cell cell){
        boolean changed = false;

        changed |= cornerPattern(cell, MyBoolean.FALSE);

        if (!cell.hasState()) {
            for (Cell adjacentCell : getNeighbors(cell)) {
                if (adjacentCell.hasState()) {
                    changed |= setStateForCell(cell, adjacentCell.getState());
                    break;
                }
            }
        }

        if (cell.hasState()) {
            changed |= colorAdjacentCells(cell, cell.getState());
        }
        return changed;
    }

    public boolean onePatterns(Cell cell){
        boolean changed = false;

        changed |= cornerPattern(cell, MyBoolean.FALSE);

        return changed;
    }

    public boolean twoPatterns(Cell cell){
        return false;
    }

    public boolean threePatterns(Cell cell){
        boolean changed = false;

        changed |= cornerPattern(cell, MyBoolean.TRUE);

        return changed;
    }

    public boolean cornerPattern(Cell cell, MyBoolean state) {
        if (!cornerCells.contains(cell)) {
            return false;
        }
        return setStateForCell(cell, state);
    }

    public ArrayList<Cell> getCornerCells() {
        ArrayList <Cell> cornerCells = new ArrayList<>();
        cornerCells.add(grid.getCells().getFirst().getFirst());
        cornerCells.add(grid.getCells().getFirst().getLast());
        cornerCells.add(grid.getCells().getLast().getFirst());
        cornerCells.add(grid.getCells().getLast().getLast());
        return cornerCells;
    }

    private boolean colorAdjacentCells(Cell cell, MyBoolean value) {
        boolean changed = false;
        for (Cell adjacentCell : getNeighbors(cell)) {
            if (!adjacentCell.hasState()) {
                changed |= setStateForCell(adjacentCell, value);
            }
        }
        return changed;
    }

    private ArrayList<Cell> getNeighbors(Cell cell) {
        return neighbors.get(cell);
    }
}
