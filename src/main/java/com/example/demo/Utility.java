package com.example.demo;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utility {
    private static HashMap<String,Long> start = new HashMap<>();
    private static HashMap<String,Long> counter = new HashMap<>();

    public static void resetTimer(String identifier){
        counter.put(identifier,0L);
    }
    public static void startTimer(String identifier){
        start.put(identifier,System.currentTimeMillis());
    }
    public static void stopTimer(String identifier){
        counter.put(identifier,counter.getOrDefault(identifier,0L)+System.currentTimeMillis()-start.getOrDefault(identifier,0L));
    }
    public static void printTimers(){
        counter.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(x->System.out.println(x.getKey()+" = "+x.getValue()+"ms"));
    }

    public static Color hex2Rgb(String colorStr) {
        if(colorStr == null || colorStr.isBlank()) return null;
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public static int levenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
}
