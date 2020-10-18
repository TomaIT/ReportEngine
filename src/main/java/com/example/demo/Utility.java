package com.example.demo;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;
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

    public static void drawRect(PDPageContentStream content, Color color, PDRectangle rect, boolean fill) throws IOException {
        content.addRect(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getWidth(), rect.getHeight());
        if (fill) {
            content.setNonStrokingColor(color);
            content.fill();
        } else {
            content.setStrokingColor(color);
            content.stroke();
        }
    }

    public static Color hex2Rgb(String colorStr) {
        if(colorStr == null || colorStr.isBlank()) return null;
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public static float getHeight(PDType1Font pdType1Font, float fontSize) {
        return (float) (((double)pdType1Font.getFontDescriptor().getCapHeight() * fontSize) / 1000.0);
    }

    public static float getWidth(String text, PDType1Font pdType1Font, float fontSize) {
        try {
            return (float) (((double)pdType1Font.getStringWidth(text)*fontSize)/1000.0);
        } catch (IOException e) {
            return 0f;
        }
    }
}
