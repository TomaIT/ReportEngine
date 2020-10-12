package com.example.demo.reportengine;

import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class Component {
    private PDRectangle pdRectangle;
    private Color borderColor;
    private List<Component> components = new ArrayList<>();

    public Component(PDRectangle pdRectangle, Color borderColor){
        this.pdRectangle = pdRectangle;
        this.borderColor = borderColor;
    }

    public void addComponent(Component component){
        // TODO There's bug if(components.stream().anyMatch(x->x.isOverlapped(component)))throw new RuntimeException("Component is overlapped with other Component");
        components.add(component);
    }

    /**
     * Find the first empty rectangle on the page.
     * null if there are none
     * @return
     */
    public PDRectangle getFirstVoidSpace(){
        float width = pdRectangle.getWidth(); // Assumption is immutable
        float x = pdRectangle.getLowerLeftX(); // Assumption is immutable

        if(components.size() <= 0) return new PDRectangle(x,pdRectangle.getLowerLeftY(),width,pdRectangle.getWidth());

        components.sort((a,b)-> Double.compare(b.getPdRectangle().getUpperRightY(), a.getPdRectangle().getUpperRightY()));

        float height = pdRectangle.getUpperRightY() - components.get(0).pdRectangle.getUpperRightY();

        if(height > 0f) return new PDRectangle(x,components.get(0).pdRectangle.getUpperRightY(),width,height);

        for (int i=1;i<components.size();i++) {
            height = components.get(i-1).pdRectangle.getLowerLeftY() - components.get(i).pdRectangle.getUpperRightY();
            if(height > 0f) return new PDRectangle(x,components.get(i).pdRectangle.getUpperRightY(),width,height);
        }

        height = components.get(components.size()-1).pdRectangle.getLowerLeftY() - pdRectangle.getLowerLeftY();

        if(height > 0f) return new PDRectangle(x,pdRectangle.getLowerLeftY(),width,height);

        return null;
    }

    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        final float lineWidth = 1.5f;
        pdPageContentStream.setLineWidth(lineWidth);
        pdPageContentStream.setStrokingColor(borderColor);
        pdPageContentStream.addRect(pdRectangle.getLowerLeftX(), pdRectangle.getLowerLeftY(), pdRectangle.getWidth(), pdRectangle.getHeight());
        pdPageContentStream.stroke();

        for(Component component : components) component.render(pdPageContentStream);
    }

    public boolean isOverlapped(Component component){
        double a = component.pdRectangle.getUpperRightX() - pdRectangle.getLowerLeftX(); // >0
        double b = pdRectangle.getUpperRightX() - component.pdRectangle.getLowerLeftX(); // >0
        double c = component.pdRectangle.getUpperRightY() - pdRectangle.getLowerLeftY(); // >0
        double d = pdRectangle.getUpperRightY() - pdRectangle.getLowerLeftY(); // >0
        //System.out.println(a+" "+b+" "+c+" "+d);
        return a > 0 && b > 0 && c > 0 && d > 0;
    }
}
