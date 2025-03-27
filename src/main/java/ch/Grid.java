package ch;

import java.util.*;

//TODO maby change 2d to 1d array
public class Grid {
    private Stack<Action> actionStack = new Stack<>();
    private ArrayList<ArrayList<Cell>> cells;
    private ArrayList<Cell> insideCells = new ArrayList<>();



    public Grid() {
        initializeCells();
    }

    public void initializeCells() {
        for (int i = 0; i < Settings.gridRows; i++) {
            var row = new ArrayList<Cell>();
            for (int j = 0; j < Settings.gridCols; j++) {
                row.add(new Cell(i, j));
            }
            cells.add(row);
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
        var directions = Settings.directions;
        var insidePercentage = Settings.insidePercentage;
        var projectedInsideCellCount = Settings.cellCount * insidePercentage;
        var initialInsideCell = cells.get(random.nextInt(cells.size())).get(random.nextInt(cells.getFirst().size()));

        initialInsideCell.setInside(true);
        insideCells.add(initialInsideCell);

        while (insideCells.size() < projectedInsideCellCount && failCount < Settings.failCount){
            var randomInsideCell = insideCells.get(random.nextInt(insideCells.size()));

            var adjacentCells = new ArrayList<Cell>(); // getAdjacentCells(randomInsideCell, directions);
            var weights = new ArrayList<Integer>(); // weightCell(randomInsideCell, adjacentCells, directions);
            var adjacentWeightedCells = new HashMap<Cell, Integer>();

            for (int i = 0; i > adjacentCells.size(); i++) {
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

    public boolean allConnected(int insideCount){
        var foundOutsideCells = new ArrayList<Cell>(); // getStartOutsideCells();

        for (Cell outsideCell : foundOutsideCells) {
            for (int[] directionValues : Settings.directions.values()){
                try {
                    var adjacentCell = cells.get(outsideCell.getRow() + directionValues[0]).get(outsideCell.getCol() + directionValues[1]);
                    if (adjacentCell.getInside() != true && !foundOutsideCells.contains(adjacentCell)) {
                        foundOutsideCells.add(adjacentCell);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return Settings.cellCount - insideCount == foundOutsideCells.size();
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
}
