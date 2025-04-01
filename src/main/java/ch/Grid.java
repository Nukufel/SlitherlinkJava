package ch;

import java.lang.reflect.Array;
import java.util.*;

public class Grid {
    private Stack<Action> actionStack = new Stack<>();
    private ArrayList<Cell> cells = new ArrayList<>();
    private ArrayList<Cell> insideCells = new ArrayList<>();
    private ArrayList<Cell> removedCells = new ArrayList<>();



    public Grid() throws CloneNotSupportedException {
        initializeCells();
        setResultBoardersForAllCells();
        setNumberForAllCells();
        removeNumbers();
    }

    public void initializeCells() {
        for (int i = 0; i < Settings.gridRows; i++) {
            for (int j = 0; j < Settings.gridCols; j++) {
                cells.add(new Cell(i, j));
            }
        }
    }

    public void undo() {
        if (!actionStack.isEmpty()) {
            Action action = actionStack.pop();
            action.getCell(); // get cell and toggle boarder twice over the set boarder method
        }
    }

    public void redo() {
        // get cell and toggle boarder twice over the set boarder method
    }

    public void initializeInsideCells(){
        Random random = new Random();

        var failCount = 0;
        var insidePercentage = Settings.insidePercentage;
        var projectedInsideCellCount = Settings.cellCount * insidePercentage;
        var initialInsideCell = cells.get(random.nextInt(cells.size()));

        initialInsideCell.setInside(true);
        insideCells.add(initialInsideCell);

        while (insideCells.size() < projectedInsideCellCount && failCount < Settings.failCount){
            var randomInsideCell = insideCells.get(random.nextInt(insideCells.size()));

            var adjacentCells = getAdjacentCells(randomInsideCell, Settings.directions);
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
                   randomWeightedCell.setInside(true);
                   if (allConnected(insideCells.size() + 1)) {
                       failCount = 0;
                       insideCells.add(randomWeightedCell);
                   } else {
                       randomWeightedCell.setInside(false);
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

        Cell randomNumberedCell = null; // get random numerd cell
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

    public ArrayList<Cell> getAdjacentCells(Cell cell, HashMap<String, Integer> directions){
        var adjacentCells = new ArrayList<Cell>();
        for (Map.Entry<String, Integer> directionEntry: directions.entrySet()) {
            if (isNextCellValid(cell, directionEntry)) {
                var adjacentCell = cells.get(cell.getId() + directionEntry.getValue());
                adjacentCells.add(adjacentCell);
            }
        }
        return adjacentCells;
    }

    public ArrayList<Integer> wightCell(ArrayList<Cell> adjacentCells, Cell baseCell){
        var weights = new ArrayList<Integer>();
        for (Cell adjacentCell : adjacentCells){
            var score = 100;
            var adjacentCellsOfAdjacentCells = getAdjacentCells(adjacentCell, Settings.directions);
            for (Cell adjacentCellsOfAdjacentCell : adjacentCellsOfAdjacentCells) {
                if (adjacentCellsOfAdjacentCell.getInside()) {
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
                    if (!adjacentCell.getInside() && !foundOutsideCells.contains(adjacentCell)) {
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
            if (nextAdjacentCell.getInside()) {
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
           if (Settings.edgeIDs.contains(cell.getId()) && !cell.getInside()) {
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
            if (!cell.isCorrect()){
                return false;
            }
        }
        return true;
    }

    public void setBoarder(Cell cell, String location, Boolean value){
        cell.toggleBoarder(location, value);
        HashMap<String, Integer> oppositeLocation = getOppositeDirection(location);
        Cell oppositeCell = getAdjacentCells(cell, oppositeLocation).getFirst();
        if (oppositeCell != null) {
            oppositeCell.toggleBoarder(getOppositeLocation(location), value);
        }
    }

    public void setResultBoarders(Cell cell, String location, Boolean value){
        cell.getResult().put(location, value);
        HashMap<String, Integer> oppositeLocation = getOppositeDirection(location);
        Cell oppositeCell = getAdjacentCells(cell, oppositeLocation).getFirst();
        if (oppositeCell != null) {
            oppositeCell.getResult().put(getOppositeLocation(location), value);
        }
    }

    public HashMap<String, Integer> getOppositeDirection(String location){
            var oppositeDirectionValue = Settings.directions.get("bottom");
            var oppositeDirection = new HashMap<String, Integer>();
            oppositeDirection.put(getOppositeLocation(location), oppositeDirectionValue);
            return oppositeDirection;
    }

    public void setResultBoardersForAllCells(){
        for (Cell cell : cells) {
            if (cell.getInside()){
                for (Map.Entry<String, Integer> direction : Settings.directions.entrySet()) {
                    HashMap<String, Integer> directionMap = new HashMap<>();
                    directionMap.put(direction.getKey(), direction.getValue());
                    Cell adjacentCell = getAdjacentCells(cell, directionMap).getFirst();
                    if (adjacentCell == null || !adjacentCell.getInside()) {
                        setResultBoarders(cell, direction.getKey(), true);
                    }
                }
            }
        }
    }

    public String getOppositeLocation(String location){
        if (location.equals("top")) {
            return "bottom";
        }
        if (location.equals("right")) {
            return "left";
        }
        if (location.equals("bottom")) {
            return "top";
        }
        if (location.equals("left")) {
            return "right";
        }
        return null;
    }

    public void setCellsUnidentified(){
        for (Cell cell : cells) {
            cell.setInside(null);
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
