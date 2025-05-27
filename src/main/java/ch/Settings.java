package ch;

import java.util.*;

public class Settings {
    public static int cellSize = 50;
    public static int gridRows = 10, gridCols = 10;
    public static int cellCount = gridRows * gridCols;
    public static int failCount = 2000;
    public static HashMap<String, Integer> directions = calculateDirections();
    public static float[] insidePercentageRange = {58, 60};
    public static float insidePercentage = calculateInsidePercentage();
    public static int removeAmount = (int) (cellCount * 0.3);
    public static int fastRemoveAmount = (int) (cellCount * 0.4);
    public static ArrayList<Integer> cornerIDs = calculateCornerIds();
    public static ArrayList<Integer> edgeIDs = calculateEdgeIDs();
    public static ArrayList<Integer> topIDs = calculateTopIDs();
    public static ArrayList<Integer> rightIDs = calculateRightIDs();
    public static ArrayList<Integer> bottomIDs = calculateBottomIDs();
    public static ArrayList<Integer> leftIDs = calculateLeftIDs();

    public static ArrayList<Integer> calculateCornerIds(){
        var myList = new ArrayList<Integer>();
        myList.add(0);
        myList.add(gridCols - 1);
        myList.add(cellCount - gridCols);
        myList.add(cellCount - 1);
        return myList;
    }

    public static HashMap<String, Integer> calculateDirections(){
        var myMap = new HashMap<String, Integer>();
        myMap.put("top", -gridRows);
        myMap.put("right", 1);
        myMap.put("bottom", gridRows);
        myMap.put("left", -1);
        return myMap;
    }

    public static float calculateInsidePercentage()
    {
        Random random = new Random();
        return (random.nextFloat(insidePercentageRange[1] - insidePercentageRange[0] + 1) + insidePercentageRange[0]) / 100;
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
        for (int i = 0; i < gridRows; i++) {
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
        for (int i = 0; i < gridRows; i++) {
            leftIDs.add(i * gridCols);
        }
        return leftIDs;
    }
}
