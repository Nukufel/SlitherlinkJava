package ch;

import java.util.*;

public class Grid {
    private Stack<Action> actionStack = new Stack<>();
    private ArrayList<Cell> cells = new ArrayList<>();
    private ArrayList<Cell> insideCells = new ArrayList<>();
    private ArrayList<Cell> removedCells = new ArrayList<>();
    public ArrayList<Boarder> boarders = new ArrayList<>();



    public Grid() throws CloneNotSupportedException {
        initializeCells();
    }

    public void initializeCells() { //TODO make all the boarders and add them to the cells
        int boarderCount = 0;

        for (int i = 0; i < Settings.gridRows; i++) {
            for (int j = 0; j < Settings.gridCols; j++) {
                var cell = new Cell(i, j);

                if (cell.getId() == 0){
                    Location loc = Location.TOP;
                    for (int h = 0; i < 4; i++){
                        var boarder = new Boarder(boarderCount);
                        boarder.addCellId(cell.getId());
                        cell.addBoarder(loc, boarder);
                        boarderCount++;
                        loc = Location.getNext(loc);
                    }
                } else if (Settings.topIDs.contains(cell.getId())){
                    var lastCell = cells.getLast();
                    var sharedBoarder = lastCell.getBoarderByLocation(Location.RIGHT);
                    sharedBoarder.addCellId(cell.getId());
                    cell.addBoarder(Location.LEFT, sharedBoarder);

                    Location loc = Location.TOP;
                    for (int h = 0; h < 3; h++){
                        var boarder = new Boarder(boarderCount);
                        boarder.addCellId(cell.getId());
                        cell.addBoarder(loc, boarder);
                        boarderCount++;
                        loc = Location.getNext(loc);
                    }
                } else if (Settings.leftIDs.contains(cell.getId())){
                    var cellAbove = cells.get(cell.getId() - Settings.gridCols);
                    var sharedBoarder = cellAbove.getBoarderByLocation(Location.BOTTOM);
                    sharedBoarder.addCellId(cell.getId());
                    cell.addBoarder(Location.TOP, sharedBoarder);

                    Location loc = Location.RIGHT;
                    for (int h = 0; h < 3; h++){
                        var boarder = new Boarder(boarderCount);
                        boarder.addCellId(cell.getId());
                        cell.addBoarder(loc, boarder);
                        boarderCount++;
                        loc = Location.getNext(loc);
                    }
                } else {
                    var lastCell = cells.getLast();
                    var sharedBoarder1 = lastCell.getBoarderByLocation(Location.RIGHT);
                    sharedBoarder1.addCellId(cell.getId());
                    cell.addBoarder(Location.LEFT, sharedBoarder1);

                    var cellAbove = cells.get(cell.getId() - Settings.gridCols);
                    var sharedBoarder2 = cellAbove.getBoarderByLocation(Location.BOTTOM);
                    sharedBoarder2.addCellId(cell.getId());
                    cell.addBoarder(Location.TOP, sharedBoarder2);

                    Location loc = Location.RIGHT;
                    for (int h = 0; h < 2; h++){
                        var boarder = new Boarder(boarderCount);
                        boarder.addCellId(cell.getId());
                        cell.addBoarder(loc, boarder);
                        boarderCount++;
                        loc = Location.getNext(loc);
                    }
                }
                cells.add(cell);
            }
        }
    }


    public void initializeInsideCells(){
        Random random = new Random();

        var failCount = 0;
        var insidePercentage = Settings.insidePercentage;
        var projectedInsideCellCount = Settings.cellCount * insidePercentage;
        var initialInsideCell = cells.get(random.nextInt(cells.size()));

        initialInsideCell.setIsInside(MyBoolean.TRUE);
        insideCells.add(initialInsideCell);

        while (insideCells.size() < projectedInsideCellCount && failCount < Settings.failCount){
            var randomInsideCell = insideCells.get(random.nextInt(insideCells.size()));

            var adjacentCells = getAdjacentCells(randomInsideCell);
            var weights = wightCell(adjacentCells, randomInsideCell);
            var adjacentWeightedCells = new HashMap<Cell, Integer>();

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
                   insideCells.add(randomWeightedCell);
               } else {
                   failCount++;
               }
            }
        }
    }

    public void removeNumbers() throws CloneNotSupportedException {
        boolean isDone = false;
        int removeAmount = Settings.removeAmount;
        Grid copiedGrid = this.clone();
        var solver = new Solver(copiedGrid, this);

        for (int i = 0; i < Math.pow(removeAmount, 5); i++) {
            ArrayList<Cell> numbersToRemove =  copiedGrid.removeNumber(solver, removeAmount);
            if (!numbersToRemove.isEmpty()) {
                for (Cell copiedCell : numbersToRemove){
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

    public ArrayList<Cell> removeNumber(Solver solver, int removeAmount){
        boolean fastRemove = false;


        if (removeAmount > Settings.removeAmount - Settings.fastRemoveAmount) {
            fastRemove = true;
        }

        if (removedCells.size() <= 0) {
            return removedCells;
        }

        Cell randomNumberedCell = getRandomNumberedCell(); // get random numerd cell
        Integer number = randomNumberedCell.getValue();
        randomNumberedCell.setShowValue(false);

        removedCells.add(randomNumberedCell);

        if (fastRemove || solver.hasSingleSolution()){
            if (!removeNumber(solver, removeAmount-1).isEmpty()){
                return removedCells;
            }
        }

        removedCells.remove(randomNumberedCell);
        randomNumberedCell.setValue(number);
        randomNumberedCell.setShowValue(true);
        return removedCells;

    }

    public Cell getRandomNumberedCell() {
        ArrayList<Cell> numberedCells = (ArrayList<Cell>) cells.stream().filter(x -> x.hasValue()).toList();
        return numberedCells.get(new Random().nextInt(numberedCells.size()-1));
    }

    public ArrayList<Cell> getAdjacentCells(Cell cell){
        var adjacentCells = new ArrayList<Cell>();
        for (Location location : Location.values()) {
            var adjacentCell = getCellByBoarder(cell, location);
            if (adjacentCell != null) {
                adjacentCells.add(adjacentCell);
            }
        }
        return adjacentCells;
    }

    public ArrayList<Integer> wightCell(ArrayList<Cell> adjacentCells, Cell baseCell){
        var weights = new ArrayList<Integer>();
        for (Cell adjacentCell : adjacentCells){
            var score = 100;
            var adjacentCellsOfAdjacentCells = getAdjacentCells(adjacentCell);
            for (Cell adjacentCellsOfAdjacentCell : adjacentCellsOfAdjacentCells) {
                if (adjacentCellsOfAdjacentCell.getIsInside() == MyBoolean.TRUE) {
                    score -= 22;
                }
            }
            score -= calculateConsecutiveInsideCellsCount(adjacentCell, baseCell);
            score += new Random().nextInt(5 - (-5)) + (-5);

            if (score < 0){
                score = 0;
            }
            weights.add(score);
        }
        return weights;
    }

    public boolean allConnected(int insideCount){

        var foundOutsideCells = getStartOutsideCells();

        for (Cell outsideCell : foundOutsideCells) {
            for (Integer directionValue : Settings.directions.values()) {
                try {
                    var adjacentCell = cells.get(outsideCell.getId() + directionValue);
                    if (adjacentCell.getIsInside() == MyBoolean.FALSE && !foundOutsideCells.contains(adjacentCell)) {
                        foundOutsideCells.add(adjacentCell);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return Settings.cellCount - insideCount == foundOutsideCells.size();
    }

    public int calculateConsecutiveInsideCellsCount(Cell adjacentCell, Cell baseCell){
        var count = 1;
        var opositeDirection = baseCell.getId() - adjacentCell.getId();
        var nextAdjacentCell = adjacentCell;
        while (true) {
            if (!isNextCellValid(nextAdjacentCell, Settings.directions.entrySet().stream().filter(e -> e.getValue() == opositeDirection).findFirst().get())) {
                break;
            }
            nextAdjacentCell = cells.get(nextAdjacentCell.getId() + opositeDirection);
            if (nextAdjacentCell.getIsInside() == MyBoolean.TRUE) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public int getTotalWeight(ArrayList<Integer> weights){
        var totalWeight = 0;
        for (var weight : weights) {
            totalWeight += weight;
        }
        return totalWeight;
    }

    public Cell getRandomWeightedCell(HashMap<Cell, Integer> weightedCells){
        var totalWeight = getTotalWeight(new ArrayList<>(weightedCells.values()));
        var random = new Random();
        var randomWeight = random.nextInt(totalWeight);
        var currentWeight = 0;

        for (var cell : weightedCells.keySet()) {
            currentWeight += weightedCells.get(cell);
            if (currentWeight >= randomWeight) {
                return cell;
            }
        }
        return null;
    }

    public ArrayList<Cell> getStartOutsideCells(){
       var startOutsideCell = new ArrayList<Cell>();
       for (var cell : cells) {
           if (Settings.edgeIDs.contains(cell.getId()) && cell.getIsInside() == MyBoolean.TRUE) {
               startOutsideCell.add(cell);
           }
       }
       return startOutsideCell;
    }

    public boolean isNextCellValid(Cell cell, Map.Entry<String, Integer> directionEntry){
        if (Settings.topIDs.contains(cell.getId()) && directionEntry.getKey().equals("top")) {
            return false;
        }
        if (Settings.rightIDs.contains(cell.getId()) && directionEntry.getKey().equals("right")) {
            return false;
        }
        if (Settings.bottomIDs.contains(cell.getId()) && directionEntry.getKey().equals("bottom")) {
            return false;
        }
        if (Settings.leftIDs.contains(cell.getId()) && directionEntry.getKey().equals("left")) {
            return false;
        }
        return true;
    }

    public void setNumberForAllCells(){
        for (Cell cell : cells) {
            cell.calcValue();
        }

    }

    public boolean isSolved(){
        for (Cell cell : cells) {
            if (!cell.isCellCorrect()){
                return false;
            }
        }
        return true;
    }

    public void setBoarder(Cell cell, Location location, MyBoolean state){
        cell.getBoarderByLocation(location).setState(state);
    }

    public void setResultBoardersForAllCells(){
        for (Cell cell : cells) {
            if (cell.getIsInside() == MyBoolean.TRUE) {
               for (Location location : Location.values()) {
                   var adjacentCell = getCellByBoarder(cell, location);
                   if (adjacentCell == null || adjacentCell.getIsInside() == MyBoolean.FALSE) {
                       cell.getBoarderByLocation(location).setResult(MyBoolean.TRUE);
                   }
               }
            }
        }
    }

    public Cell getCellByBoarder(Cell cell, Location location){
        var boarder = cell.getBoarderByLocation(location);
        var otherCellId = boarder.getOtherCellId(cell.getId());
        if (otherCellId == null) {
            return null;
        }
        return cells.get(otherCellId);
    }


    public void setCellsUnidentified(){
        for (Cell cell : cells) {
            cell.setIsInside(MyBoolean.NULL);
        }
    }

    @Override
    protected Grid clone() throws CloneNotSupportedException {
        Grid grid = new Grid();
        grid.cells = new ArrayList<>();
        for (Cell cell : cells) {
            grid.cells.add((Cell) cell.clone());
        }
        grid.insideCells = new ArrayList<>();
        for (Cell cell : insideCells) {
            grid.insideCells.add((Cell) cell.clone());
        }
        grid.removedCells = new ArrayList<>();
        for (Cell cell : removedCells) {
            grid.removedCells.add((Cell) cell.clone());
        }
        return grid;
    }

    public Stack<Action> getActionStack() {
        return actionStack;
    }

    public void setActionStack(Stack<Action> actionStack) {
        this.actionStack = actionStack;
    }

    public ArrayList<Cell> getRemovedCells() {
        return removedCells;
    }

    public void setRemovedCells(ArrayList<Cell> removedCells) {
        this.removedCells = removedCells;
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
