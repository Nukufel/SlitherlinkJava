package ch;

import java.util.*;

public class Settings {
    public static int cellSize = 50;
    public static int gridRows = 5, gridCols = 5;
    public static int cellCount = gridRows * gridCols;
    public static int failCount = 2000;
    public static HashMap<String, Integer> directions = new HashMap<>();
    public static int[] insidePercentageRange = {58, 60};
    public static int insidePercentage;
    public static int removeAmount = (int) (cellCount * 0.5);
    public static int fastRemoveAmount = (int) (cellCount * 0.4);
    public static ArrayList<Integer> cornerIDs = new ArrayList<>();
    public static ArrayList<Integer> edgeIDs = new ArrayList<>();
    public static ArrayList<Integer> topIDs = new ArrayList<>();
    public static ArrayList<Integer> rightIDs = new ArrayList<>();
    public static ArrayList<Integer> bottomIDs = new ArrayList<>();
    public static ArrayList<Integer> leftIDs = new ArrayList<>();

    public Settings(){
        directions.put("top", -gridRows);
        directions.put("right", 1);
        directions.put("bottom", gridRows);
        directions.put("left", -1);

        cornerIDs.add(0);
        cornerIDs.add(gridCols - 1);
        cornerIDs.add(cellCount - gridCols);
        cornerIDs.add(cellCount - 1);

        Random random = new Random();
        insidePercentage = (random.nextInt(insidePercentageRange[1] - insidePercentageRange[0] + 1) + insidePercentageRange[0]) / 100;
        edgeIDs = calculateEdgeIDs();
        topIDs = calculateTopIDs();
        rightIDs = calculateRightIDs();
        bottomIDs = calculateBottomIDs();
        leftIDs = calculateLeftIDs();
    }

    public static ArrayList<Integer> calculateEdgeIDs() {
        var edgeIDs = new ArrayList<Integer>();

        edgeIDs.addAll(calculateTopIDs());
        edgeIDs.addAll(calculateRightIDs());
        edgeIDs.addAll(calculateBottomIDs());
        edgeIDs.addAll(calculateLeftIDs());

        return edgeIDs;
    }

    public static ArrayList<Integer> calculateTopIDs() {
        var topIDs = new ArrayList<Integer>();
        for (int i = 0; i < gridCols; i++) {
            topIDs.add(i);
        }
        return topIDs;
    }

    public static ArrayList<Integer> calculateRightIDs() {
        var rightIDs = new ArrayList<Integer>();
        for (int i = 1; i < gridRows - 1; i++) {
            rightIDs.add(i * gridCols + gridCols - 1);
        }
        return rightIDs;
    }

    public static ArrayList<Integer> calculateBottomIDs() {
        var bottomIDs = new ArrayList<Integer>();
        for (int i = 0; i < gridCols; i++) {
            bottomIDs.add(cellCount - gridCols + i);
        }
        return bottomIDs;
    }

    public static ArrayList<Integer> calculateLeftIDs() {
        var leftIDs = new ArrayList<Integer>();
        for (int i = 1; i < gridRows - 1; i++) {
            leftIDs.add(i * gridCols);
        }
        return leftIDs;
    }
}
