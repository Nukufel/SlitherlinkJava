package ch;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Grid {
    private ArrayList<ArrayList<Cell>> cells = new ArrayList<>();
    private ArrayList<Border> borders = new ArrayList<>();
    private final ArrayList<Cell> insideCells = new ArrayList<>();
    private final ArrayList<Cell> cellsWithNumbersRemoved = new ArrayList<>();
    private ArrayList<ArrayList<Node>> nodes = initializeNodes();
    private final Random rand = Settings.rand;
    private Integer boarderCount = 0;


    public Grid() {
        initializeBorders();
        initializeNodes();
        initializeCells();

        addBordersToCells();
        addNodesToCells();

        initializeInsideCells();
        setResultBoardersForAllCells();
        setNumberForAllCells();

        removeNumbersForFinalGrid();
    }


    public Grid(Grid other) {
        this.nodes = new ArrayList<>();
        for (ArrayList<Node> col : other.nodes) {
            var newCol = new ArrayList<Node>();
            for (Node node : col) {
                newCol.add(new Node(node));
            }
            this.nodes.add(newCol);
        }

        this.borders = new ArrayList<>();
        for (Border border : other.borders) {
            borders.add(new Border(border));
        }

        this.cells = new ArrayList<>();
        for (ArrayList<Cell> col : other.cells) {
            var newCol = new ArrayList<Cell>();
            for (Cell cell : col) {
                newCol.add(new Cell(cell)); // Assuming Cell has a copy constructor
            }
            this.cells.add(newCol);
        }

        addBordersToCells();
        addNodesToCells();
    }

    public Grid deepCopy() {
        return new Grid(this);
    }

    public void initializeBorders(){
        int amount = (Settings.gridRows + 1) * Settings.gridCols * 2;
        for (int i = 0; i < amount; i++){
            borders.add(new Border(i));
        }
    }

    public ArrayList<ArrayList<Node>> initializeNodes(){
        var nodes = new ArrayList<ArrayList<Node>>();
        for (int i = 0; i < Settings.gridRows+1; i++) {
            var list = new ArrayList<Node>();
            for (int j = 0; j < Settings.gridCols+1; j++) {
                list.add(new Node(i, j));
            }
            nodes.add(list);
        }
        return nodes;
    }

    public void addNodesToCells(){
        for (Cell cell : getFlattenedCells()){
            var nodeTopLeft = nodes.get(cell.getRow()).get(cell.getCol());
            var nodeTopRight = nodes.get(cell.getRow()).get(cell.getCol()+1);
            var nodeBottomRight = nodes.get(cell.getRow()+1).get(cell.getCol()+1);
            var nodeBottomLeft = nodes.get(cell.getRow()+1).get(cell.getCol());

            var topBorder = cell.getBoarderByLocation(Location.TOP);
            var rightBorder = cell.getBoarderByLocation(Location.RIGHT);
            var bottomBorder = cell.getBoarderByLocation(Location.BOTTOM);
            var leftBorder = cell.getBoarderByLocation(Location.LEFT);

            nodeTopLeft.connectedBorders.add(topBorder);
            nodeTopLeft.connectedBorders.add(leftBorder);

            nodeTopRight.connectedBorders.add(topBorder);
            nodeTopRight.connectedBorders.add(rightBorder);

            nodeBottomRight.connectedBorders.add(rightBorder);
            nodeBottomRight.connectedBorders.add(bottomBorder);

            nodeBottomLeft.connectedBorders.add(bottomBorder);
            nodeBottomLeft.connectedBorders.add(leftBorder);

            topBorder.addConnectedNode(nodeTopLeft);
            topBorder.addConnectedNode(nodeTopRight);

            rightBorder.addConnectedNode(nodeTopRight);
            rightBorder.addConnectedNode(nodeBottomRight);

            bottomBorder.addConnectedNode(nodeBottomRight);
            bottomBorder.addConnectedNode(nodeBottomLeft);

            leftBorder.addConnectedNode(nodeBottomLeft);
            leftBorder.addConnectedNode(nodeTopLeft);

            cell.addCellNode(nodeTopLeft);
            cell.addCellNode(nodeTopRight);
            cell.addCellNode(nodeBottomRight);
            cell.addCellNode(nodeBottomLeft);
        }
    }

    public void initializeCells() {
        for (int i = 0; i < Settings.gridRows; i++) {
            cells.add(new ArrayList<Cell>());
        }

        for (int i = 0; i < Settings.gridRows; i++) {
            for (int j = 0; j < Settings.gridCols; j++) {
                cells.get(i).add(new Cell(i, j));
            }
        }
    }
    public void addBordersToCells(){
        Cell lastCell = cells.getFirst().getFirst();

        for (int i = 0; i < Settings.gridRows; i++) {
            for (int j = 0; j < Settings.gridCols; j++) {
                var cell = cells.get(i).get(j);

                if (i == 0 && j == 0){
                    lastCell = cell;
                }

                if (cell.getRow() + cell.getCol() == 0) {
                    makeNewBoardersForCell(cell, Location.TOP, 4);

                } else if (cell.getRow() == 0) {
                    var sharedBoarder = lastCell.getBoarderByLocation(Location.RIGHT);
                    sharedBoarder.addConnectedCell(cell);
                    cell.addBoarder(Location.LEFT, sharedBoarder);

                    makeNewBoardersForCell(cell, Location.TOP, 3);

                } else if (cell.getCol() == 0) {
                    var cellAbove = cells.get(i-1).get(cell.getCol());
                    var sharedBoarder = cellAbove.getBoarderByLocation(Location.BOTTOM);
                    sharedBoarder.addConnectedCell(cell);
                    cell.addBoarder(Location.TOP, sharedBoarder);

                    makeNewBoardersForCell(cell, Location.RIGHT, 3);

                } else {

                    var sharedBoarder1 = lastCell.getBoarderByLocation(Location.RIGHT);
                    sharedBoarder1.addConnectedCell(cell);
                    cell.addBoarder(Location.LEFT, sharedBoarder1);

                    var cellAbove = cells.get(i-1).get(cell.getCol());
                    var sharedBoarder2 = cellAbove.getBoarderByLocation(Location.BOTTOM);
                    sharedBoarder2.addConnectedCell(cell);
                    cell.addBoarder(Location.TOP, sharedBoarder2);

                    makeNewBoardersForCell(cell, Location.RIGHT, 2);

                }
                lastCell = cell;
            }
        }
    }

    public void makeNewBoardersForCell(Cell cell, Location startLocation, int amountOfNewBoarders) {
        Location loc = startLocation;
        for (int h = 0; h < amountOfNewBoarders; h++) {
            var border = borders.get(boarderCount);
            border.addConnectedCell(cell);
            cell.addBoarder(loc, border);
            boarderCount++;
            loc = Location.getNext(loc);
        }
    }

    public void initializeInsideCells() {
        var failCount = 0;
        var projectedInsideCellCount = Settings.cellCount * Settings.insidePercentage;
        var initialInsideCell = cells.get(rand.nextInt(Settings.gridRows)).get(rand.nextInt(Settings.gridCols));

        initialInsideCell.setState(MyBoolean.TRUE);
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
                    randomWeightedCell.setState(MyBoolean.TRUE);
                    if (allConnected(insideCells.size() + 1)) {
                        failCount = 0;
                        insideCells.add(randomWeightedCell);
                    } else {
                        randomWeightedCell.setState(MyBoolean.FALSE);
                    }
                } else {
                    failCount++;
                }
            }
        }
    }

    public void setNumbersInvisible(ArrayList<Cell> numbersToSetInvisible) {
        for (Cell copyedCell : numbersToSetInvisible) {
            Cell cell = cells.get(copyedCell.getRow()).get(copyedCell.getCol());
            cell.setShowValue(false);
        }
    }

    public void removeNumbersForFinalGrid(){
        final int threadCount =  1; //Settings.gridRows;
        final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        boolean isDone = false;

        try{
            List<Future<ArrayList<Cell>>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                Random random = new Random(Settings.randomSeed + i);
                RemovalWorker removalWorker = new RemovalWorker(this, random);
                futures.add(executor.submit(removalWorker));
            }

            for (Future<ArrayList<Cell>> future : futures) {
                ArrayList<Cell> result = future.get(); // waits for the result
                if (result != null && !result.isEmpty()) {
                    setNumbersInvisible(result);
                    isDone = true;
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.getCause().printStackTrace();
        } finally {
            executor.shutdownNow();
        }

        if (!isDone) {
            System.out.println("Failed to remove numbers, no unique solution");
        }
    }

    public ArrayList<Cell> getNumbersToRemove(Random rand) {
        int removeAmount = Settings.removeAmount;
        Grid copiedGrid = deepCopy();
        var solver = new Solver(copiedGrid);

        for (int i = 0; i < Math.pow(removeAmount, 5); i++) {
            ArrayList<Cell> cellsOfNumbersToRemove = copiedGrid.removeNumber(solver, removeAmount, null, rand);
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }

            if (cellsOfNumbersToRemove != null && !cellsOfNumbersToRemove.isEmpty()) {
                return cellsOfNumbersToRemove;
            }
        }

        return null;
    }

    public ArrayList<Cell> removeNumber(
            Solver solver,
            int removeAmount,
            Cell lastCell,
            Random rand
    ) {
        if (removeAmount <= 0) {
            return new ArrayList<>(cellsWithNumbersRemoved);
        }

        Cell randomNumberedCell =
                getRandomNumberedCell(lastCell, rand);

        Integer number = randomNumberedCell.getValue();

        randomNumberedCell.setValue(null);
        cellsWithNumbersRemoved.add(randomNumberedCell);

        if (solver.hasSingleSolution()) {

            ArrayList<Cell> result = removeNumber(
                    solver,
                    removeAmount - 1,
                    randomNumberedCell,
                    rand
            );

            // Deeper recursion succeeded
            if (result != null) {
                return result;
            }
        }

        // Either this removal broke uniqueness
        // OR a later removal failed.
        // In BOTH cases restore this clue.
        cellsWithNumbersRemoved.remove(randomNumberedCell);
        randomNumberedCell.setValue(number);

        return null;
    }

    public Cell getRandomNumberedCell(Cell lastCell, Random rand) {
        List<Cell> numberedCells = getNumberedCells();
        Cell cell = numberedCells.get(rand.nextInt(numberedCells.size() - 1));
        if (lastCell != null && cell == lastCell && numberedCells.size() > 1) {
            return getRandomNumberedCell(lastCell, rand);
        }
        return cell;
    }

    private List<Cell> getNumberedCells(){
        var list = new ArrayList<Cell>();
        for (ArrayList<Cell> col : cells){
            for (Cell cell : col) {
                if (cell.hasValue()){
                    list.add(cell);
                }
            }
        }
        return list;
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
                if (adjacentCellsOfAdjacentCell.getState() == MyBoolean.TRUE) {
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
                if (adjacentCell.getState() == MyBoolean.FALSE && !foundOutsideCells.contains(adjacentCell)) {
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
            while (nextCell.getState() == MyBoolean.TRUE) {
                count++;
                nextCell = getCellByBoarderLocation(nextCell, loc);
                if (nextCell == null) {
                    break;
                }
            }
            loc = Location.getOppositeLocation(loc);
            nextCell = baseCell;
            while (nextCell.getState() == MyBoolean.TRUE) {
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
            Border border = baseCell.getBoarders().get(key);
            for (Cell cell: border.getConnectedCells()) {
                if (cell.equals(adjacentCell)) {
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
        for (ArrayList<Cell> col : cells) {
            for (Cell cell : col) {
                if ((cell.getRow() == 0 || cell.getCol() == 0 || cell.getRow() == Settings.gridRows -1 || cell.getCol() == Settings.gridCols -1)
                        && cell.getState() == MyBoolean.FALSE) {
                    startOutsideCell.add(cell);
                }
            }
        }
        return startOutsideCell;
    }

    public void setNumberForAllCells() {
        for (ArrayList<Cell> col : cells) {
            for (Cell cell : col) {
                cell.calcValue();
            }
        }
    }

    public boolean isSolved() {
        for (ArrayList<Cell> col : cells) {
            for (Cell cell : col) {
                if (!cell.isCellCorrect()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setResultBoardersForAllCells() {
        for (ArrayList<Cell> col : cells) {
            for (Cell cell : col) {
                if (cell.getState() == MyBoolean.TRUE) {
                    for (Location location : Location.values()) {
                        var adjacentCell = getCellByBoarderLocation(cell, location);
                        if (adjacentCell == null || adjacentCell.getState() == MyBoolean.FALSE) {
                            cell.getBoarderByLocation(location).setResult(MyBoolean.TRUE);
                        }
                    }
                }
            }
        }
    }

    public Cell getCellByBoarderLocation(Cell cell, Location location) {
        var boarder = cell.getBoarderByLocation(location);
        var otherCell = boarder.getOtherCell(cell);
        if (otherCell == null) {
            return null;
        }
        return cells.get(otherCell.getRow()).get(otherCell.getCol());
    }


    public void setCellsUnidentified() {
        for (ArrayList<Cell> col : cells) {
            for (Cell cell : col) {
                cell.setState(MyBoolean.NONE);
            }
        }
    }

    public ArrayList<Cell> getUnidentifiedCells() {
        var unidentifiedCells = new ArrayList<Cell>();
        for (ArrayList<Cell> col : cells) {
            for (Cell cell : col) {
                if (cell.getState() == MyBoolean.NONE) {
                    unidentifiedCells.add(cell);
                }
            }
        }
        return unidentifiedCells;
    }


    public ArrayList<Cell> getFlattenedCells() {
        var flattenedCells = new ArrayList<Cell>();
        for (ArrayList<Cell> col : cells) {
            flattenedCells.addAll(col);
        }
        return flattenedCells;
    }

    public ArrayList<ArrayList<Cell>> getCells() {
        return cells;
    }

    public ArrayList<Node> getFlattenedNodes() {
        var flattenedCells = new ArrayList<Node>();
        for (ArrayList<Node> col : nodes) {
            flattenedCells.addAll(col);
        }
        return flattenedCells;
    }

    public ArrayList<Border> getBorders(){
        return borders;
    }

    public ArrayList<ArrayList<Node>> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<ArrayList<Node>> nodes) {
        this.nodes = nodes;
    }
}
