package com.example.demo.reportengine;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Component {
    private PDRectangle pdRectangle;
    private Color borderColor = Color.BLACK;
    private List<Component> components = new ArrayList<>();

    public Component(PDRectangle pdRectangle, Color borderColor){
        this.pdRectangle = pdRectangle;
        this.borderColor = borderColor;
    }

    public void addComponent(Component component){
        checkOverlapping(component);
        components.add(component);
    }

    /**
     * Find the first empty rectangle on the page.
     * null if there are none
     * It assumes components whose rectangles are as wide as all available space.
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

    public void build(float startX, float startY,float maxWidth,float minHeight){
        throw new RuntimeException("Not Implemented");
    }


    public boolean isOverlapped(Component component){
        return component != null && component.pdRectangle != null && pdRectangle != null &&
                component.pdRectangle.getUpperRightX() > pdRectangle.getLowerLeftX() &&
                pdRectangle.getUpperRightX() > component.pdRectangle.getLowerLeftX() &&
                component.pdRectangle.getUpperRightY() > pdRectangle.getLowerLeftY() &&
                pdRectangle.getUpperRightY() > component.pdRectangle.getLowerLeftY();
    }

    public void checkOverlapping(Component component){
        if(components.stream().anyMatch(x->x.isOverlapped(component))) {
            throw new RuntimeException("Component is overlapped with other Component");
        }
    }
}
