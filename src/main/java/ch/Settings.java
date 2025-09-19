package ch;

import javafx.util.Pair;

import java.util.*;

public class Settings {
    public static long randomSeed = setRandomSeed(2);
    public static Random rand = new Random(randomSeed);
    public static int cellSize = 50;
    public static int gridRows = 3, gridCols = 3;
    public static int cellCount = gridRows * gridCols;
    public static int failCount = 2000;
    public static LinkedHashMap<String, Integer> directions = calculateDirections();
    public static float[] insidePercentageRange = {58, 60};
    public static float insidePercentage = calculateInsidePercentage(rand);
    //public static int removeAmount = (int) (cellCount * 0.6);
    public static int removeAmount = 2;


    private static long setRandomSeed(Integer seed) {
        if (seed != null){
            return seed;
        }
        Random random = new Random();
        return random.nextLong();
    }

    public static LinkedHashMap<String, Integer> calculateDirections(){
        var myMap = new LinkedHashMap<String, Integer>();
        myMap.put("top", -gridRows);
        myMap.put("right", 1);
        myMap.put("bottom", gridRows);
        myMap.put("left", -1);
        return myMap;
    }

    public static float calculateInsidePercentage(Random rand) {
        return (rand.nextFloat(insidePercentageRange[1] - insidePercentageRange[0] + 1) + insidePercentageRange[0]) / 100;
    }
}
