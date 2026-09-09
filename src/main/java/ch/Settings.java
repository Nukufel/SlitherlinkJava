package ch;


import java.util.*;

public class Settings {
    public static int gridRows, gridCols;
    public static int cellCount;
    public static double removePercentage;
    public static long randomSeed;
    public static Random rand;
    public static int removeAmount;

    public static int failCount;
    public static LinkedHashMap<String, Integer> directions;
    public static float[] insidePercentageRange;
    public static float insidePercentage;

    public Settings(int size, double removePercent, long seed) {
        gridRows = size;
        gridCols = size;
        cellCount = size * size;
        removePercentage = removePercent;

        rand = new Random(seed);

        removeAmount = (int) (cellCount * removePercentage);
        directions = calculateDirections();
        failCount = 2000;
        insidePercentageRange = new float[] {58, 60};
        insidePercentage = calculateInsidePercentage(rand);
    }


    private Integer setRandomSeed(Integer seed) {
        if (seed != null){
            return seed;
        }
        Random random = new Random();
        return random.nextInt();
    }

    public LinkedHashMap<String, Integer> calculateDirections(){
        var myMap = new LinkedHashMap<String, Integer>();
        myMap.put("top", -gridRows);
        myMap.put("right", 1);
        myMap.put("bottom", gridRows);
        myMap.put("left", -1);
        return myMap;
    }

    public float calculateInsidePercentage(Random rand) {
        return (rand.nextFloat(insidePercentageRange[1] - insidePercentageRange[0] + 1) + insidePercentageRange[0]) / 100;
    }
}
