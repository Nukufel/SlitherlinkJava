package ch;

import java.util.*;

public class Settings {
    public static int gridRows = 5, gridCols = 5;
    public static int cellCount = gridRows * gridCols;
    public static int failCount = 2000;
    public static HashMap<String, int[]> directions = new HashMap<>();
    public static int[] insidePercentageRange = {58, 60};
    public static int insidePercentage;

    public Settings(){
        directions.put("top", new int[]{-1, 0});
        directions.put("right", new int[]{0, 1});
        directions.put("bottom", new int[]{1, 0});
        directions.put("left", new int[]{0, -1});

        Random random = new Random();
        insidePercentage = (random.nextInt(insidePercentageRange[1] - insidePercentageRange[0] + 1) + insidePercentageRange[0]) / 100;
    }
}
