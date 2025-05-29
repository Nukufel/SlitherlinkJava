package ch;

import java.util.*;

public class Grid {
    private Stack<Action> actionStack = new Stack<>();
    private ArrayList<Cell> cells = new ArrayList<>();
    private ArrayList<Cell> insideCells = new ArrayList<>();
    private ArrayList<Cell> cellsWithNumbersRemoved = new ArrayList<>();
    private Random rand = Settings.rand;


    public Grid() {
        initializeCells();
        initializeInsideCells();
        setResultBoardersForAllCells();
        setNumberForAllCells();
        removeNumbers();
    }

    public Grid(Grid other) {
        this.actionStack = new Stack<>();
        for (Action action : other.actionStack) {
            this.actionStack.push(new Action(action)); // Assuming Action has a copy constructor
        }

        this.cells = new ArrayList<>();
        for (Cell cell : other.cells) {
            this.cells.add(new Cell(cell)); // Assuming Cell has a copy constructor
        }
    }

    public Grid deepCopy() {
        return new Grid(this);
    }

    public void initializeCells() {
        int boarderCount = 0;

        for (int i = 0; i < Settings.cellCount; i++) {
            var cell = new Cell(i);

            if (cell.getId() == 0) {
                boarderCount += makeNewBoardersForCell(cell, boarderCount, Location.TOP, 4);

            } else if (Settings.topIDs.contains(cell.getId())) {
                var lastCell = cells.getLast();
                var sharedBoarder = lastCell.getBoarderByLocation(Location.RIGHT);
                sharedBoarder.addCellId(cell.getId());
                cell.addBoarder(Location.LEFT, sharedBoarder);

                boarderCount +=  makeNewBoardersForCell(cell, boarderCount, Location.TOP, 3);

            } else if (Settings.leftIDs.contains(cell.getId())) {
                var cellAbove = cells.get(cell.getId() - Settings.gridCols);
                var sharedBoarder = cellAbove.getBoarderByLocation(Location.BOTTOM);
                sharedBoarder.addCellId(cell.getId());
                cell.addBoarder(Location.TOP, sharedBoarder);

                boarderCount += makeNewBoardersForCell(cell, boarderCount, Location.RIGHT, 3);

            } else {

                var lastCell = cells.getLast();
                var sharedBoarder1 = lastCell.getBoarderByLocation(Location.RIGHT);
                sharedBoarder1.addCellId(cell.getId());
                cell.addBoarder(Location.LEFT, sharedBoarder1);

                var cellAbove = cells.get(cell.getId() - Settings.gridCols);
                var sharedBoarder2 = cellAbove.getBoarderByLocation(Location.BOTTOM);
                sharedBoarder2.addCellId(cell.getId());
                cell.addBoarder(Location.TOP, sharedBoarder2);

                boarderCount +=  makeNewBoardersForCell(cell, boarderCount, Location.RIGHT, 2);

            }
            cells.add(cell);

        }
    }

    public int makeNewBoardersForCell(Cell cell, int boarderCount, Location startLocation, int amountOfNewBoarders) {
        Location loc = startLocation;
        for (int h = 0; h < amountOfNewBoarders; h++) {
            var boarder = new Boarder(boarderCount);
            boarder.addCellId(cell.getId());
            cell.addBoarder(loc, boarder);
            boarderCount++;
            loc = Location.getNext(loc);
        }
        return boarderCount;
    }

    public void initializeInsideCells() {
        var failCount = 0;
        var projectedInsideCellCount = Settings.cellCount * Settings.insidePercentage;
        var initialInsideCell = cells.get(rand.nextInt(cells.size()));

        initialInsideCell.setIsInside(MyBoolean.TRUE);
        insideCells.add(initialInsideCell);


        while (insideCells.size() < projectedInsideCellCount && failCount < Settings.failCount) {
            var randomInsideCell = insideCells.get(rand.nextInt(insideCells.size()));

            var adjacentCells = getAdjacentCells(randomInsideCell);
            var weights = weightCell(adjacentCells, randomInsideCell);
            var adjacentWeightedCells = new LinkedHashMap<Cell, Integer>();

            for (int i = 0; i < adjacentCells.size(); i++) {
                if (weights.get(i) > 50) {
                    adjacentWeightedCells.put(adjacentCells.get(i), weights.get(i));
                }
            }

            if (!adjacentWeightedCells.isEmpty()) {
                var randomWeightedCell = getRandomWeightedCell(adjacentWeightedCells);
                if (!insideCells.contains(randomWeightedCell)) {
                    randomWeightedCell.setIsInside(MyBoolean.TRUE);
                    if (allConnected(insideCells.size() + 1)) {
                        failCount = 0;
                        insideCells.add(randomWeightedCell);
                    } else {
                        randomWeightedCell.setIsInside(MyBoolean.FALSE);
                    }
                } else {
                    failCount++;
                }
            }
        }
    }

    public void removeNumbers() {
        boolean isDone = false;
        int removeAmount = Settings.removeAmount;
        Grid copiedGrid = deepCopy();
        var solver = new Solver(copiedGrid, this);

        for (int i = 0; i < Math.pow(removeAmount, 5); i++) {
            ArrayList<Cell> cellsOfNumbersToRemove = copiedGrid.removeNumber(solver, removeAmount, null);

            if (cellsOfNumbersToRemove != null && !cellsOfNumbersToRemove.isEmpty()) {
                for (Cell copiedCell : cellsOfNumbersToRemove) {
                    Cell cell = cells.get(copiedCell.getId());
                    cell.setShowValue(false);
                }
                isDone = true;
                break;
            }
        }

        if (!isDone) {
            System.out.print("Failed to remove numbers, no unique solution");
        }
    }

    public ArrayList<Cell> removeNumber(Solver solver, int removeAmount, Cell lastCell) {
        if (removeAmount <= 0) {
            return cellsWithNumbersRemoved;
        }

        Cell randomNumberedCell = getRandomNumberedCell(lastCell);
        Integer number =  randomNumberedCell.getValue();

        randomNumberedCell.setValue(null);
        cellsWithNumbersRemoved.add(randomNumberedCell);

        if (solver.hasSingleSolution()) {
            if (removeNumber(solver, removeAmount - 1, randomNumberedCell) != null) {
                return cellsWithNumbersRemoved;
            }
        }

        cellsWithNumbersRemoved.remove(randomNumberedCell);
        randomNumberedCell.setValue(number);


        return null;

    }

    public Cell getRandomNumberedCell(Cell lastCell) {
        List<Cell> numberedCells = cells.stream().filter(x -> x.hasValue()).toList();
        Cell cell = numberedCells.get(rand.nextInt(numberedCells.size() - 1));
        if (lastCell != null && cell.getId() == lastCell.getId() && numberedCells.size() > 1) {
            return getRandomNumberedCell(lastCell);
        }
        return cell;
    }

    public ArrayList<Cell> getAdjacentCells(Cell cell) {
        var adjacentCells = new ArrayList<Cell>();
        for (Location location : Location.values()) {
            var adjacentCell = getCellByBoarderLocation(cell, location);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        return adjacentCells;
    }

    public ArrayList<Integer> weightCell(ArrayList<Cell> adjacentCells, Cell baseCell) {
        var weights = new ArrayList<Integer>();
        for (Cell adjacentCell : adjacentCells) {
            var score = 100;
            var adjacentCellsOfAdjacentCells = getAdjacentCells(adjacentCell);
            for (Cell adjacentCellsOfAdjacentCell : adjacentCellsOfAdjacentCells) {
                if (adjacentCellsOfAdjacentCell.getIsInside() == MyBoolean.TRUE) {
                    score -= 22;
                }
            }
            score -= calculateConsecutiveInsideCellsCount(adjacentCell, baseCell);
            score += rand.nextInt(5 - (-5)) + (-5);

            if (score < 0) {
                score = 0;
            }
            weights.add(score);
        }
        return weights;
    }

    public boolean allConnected(int insideCount) {

        var foundOutsideCells = getStartOutsideCells();

        for (int i = 0; i < foundOutsideCells.size(); i++) {
            for (Cell adjacentCell : getAdjacentCells(foundOutsideCells.get(i))) {
                if (adjacentCell.getIsInside() == MyBoolean.FALSE && !foundOutsideCells.contains(adjacentCell)) {
                    foundOutsideCells.add(adjacentCell);
                }
            }
        }
        return Settings.cellCount - insideCount == foundOutsideCells.size();
    }

    public int calculateConsecutiveInsideCellsCount(Cell adjacentCell, Cell baseCell) {
        Location loc = getBorderLocationFromBaseToAdjacentCell(baseCell, adjacentCell);

        if (loc != null) {
            int count = 0;
            Cell nextCell = baseCell;
            while (nextCell.getIsInside() == MyBoolean.TRUE) {
                count++;
                nextCell = getCellByBoarderLocation(nextCell, loc);
                if (nextCell == null) {
                    break;
                }
            }
            loc = Location.getOppositeLocation(loc);
            nextCell = baseCell;
            while (nextCell.getIsInside() == MyBoolean.TRUE) {
                count++;
                nextCell = getCellByBoarderLocation(nextCell, loc);
                if (nextCell == null) {
                    break;
                }
            }

            return count * count;
        }
        return 0;
    }

    public Location getBorderLocationFromBaseToAdjacentCell(Cell baseCell, Cell adjacentCell) {
        Location location = null;
        for (var key : baseCell.getBoarders().keySet()){
            Boarder border = baseCell.getBoarders().get(key);
            for (Integer id: border.getCellIds()) {
                if (adjacentCell.getId() == id) {
                    location = key;
                }
            }
        }
        return location;
    }



    public int getTotalWeight(ArrayList<Integer> weights) {
        var totalWeight = 0;
        for (var weight : weights) {
            totalWeight += weight;
        }
        return totalWeight;
    }

    public Cell getRandomWeightedCell(LinkedHashMap<Cell, Integer> weightedCells) {
        var totalWeight = getTotalWeight(new ArrayList<>(weightedCells.values()));
        var randomWeight = rand.nextInt(totalWeight);
        var currentWeight = 0;

        for (var cell : weightedCells.keySet()) {
            currentWeight += weightedCells.get(cell);
            if (currentWeight >= randomWeight) {
                return cell;
            }
        }
        return null;
    }

    public ArrayList<Cell> getStartOutsideCells() {
        var startOutsideCell = new ArrayList<Cell>();
        for (Cell cell : cells) {
            if (Settings.edgeIDs.contains(cell.getId()) && cell.getIsInside() == MyBoolean.FALSE) {
                startOutsideCell.add(cell);
            }
        }
        return startOutsideCell;
    }

    public void setNumberForAllCells() {
        for (Cell cell : cells) {
            cell.calcValue();
        }
    }

    public boolean isSolved() {
        for (Cell cell : cells) {
            if (!cell.isCellCorrect()) {
                return false;
            }
        }
        return true;
    }

    public void setResultBoardersForAllCells() {
        for (Cell cell : cells) {
            if (cell.getIsInside() == MyBoolean.TRUE) {
                for (Location location : Location.values()) {
                    var adjacentCell = getCellByBoarderLocation(cell, location);
                    if (adjacentCell == null || adjacentCell.getIsInside() == MyBoolean.FALSE) {
                        cell.getBoarderByLocation(location).setResult(MyBoolean.TRUE);
                    }
                }
            }
        }
    }

    public Cell getCellByBoarderLocation(Cell cell, Location location) {
        var boarder = cell.getBoarderByLocation(location);
        var otherCellId = boarder.getOtherCellId(cell.getId());
        if (otherCellId == null) {
            return null;
        }
        return cells.get(otherCellId);
    }


    public void setCellsUnidentified() {
        for (Cell cell : cells) {
            cell.setIsInside(MyBoolean.NULL);
        }
    }

    public ArrayList<Cell> getUnidentifiedCells() {
        var unidentifiedCells = new ArrayList<Cell>();
        for (Cell cell : cells) {
            if (cell.getIsInside() == MyBoolean.NULL) {
                unidentifiedCells.add(cell);
            }
        }
        return unidentifiedCells;
    }

    public ArrayList<Cell> getCopyOfCells(){
        return new ArrayList<>(cells);
    }


    public Stack<Action> getActionStack() {
        return actionStack;
    }

    public void setActionStack(Stack<Action> actionStack) {
        this.actionStack = actionStack;
    }

    public ArrayList<Cell> getCellsWithNumbersRemoved() {
        return cellsWithNumbersRemoved;
    }

    public void setCellsWithNumbersRemoved(ArrayList<Cell> cellsWithNumbersRemoved) {
        this.cellsWithNumbersRemoved = cellsWithNumbersRemoved;
    }

    public ArrayList<Cell> getInsideCells() {
        return insideCells;
    }

    public void setInsideCells(ArrayList<Cell> insideCells) {
        this.insideCells = insideCells;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public void setCells(ArrayList<Cell> cells) {
        this.cells = cells;
    }
}
