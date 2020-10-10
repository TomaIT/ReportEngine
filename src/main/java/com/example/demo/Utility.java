package com.example.demo;

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
                .forEach(System.out::println);
    }
}
