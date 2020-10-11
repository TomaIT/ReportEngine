package com.example.demo.reportengine;

import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Component {
    private PDRectangle pdRectangle;
    private List<Component> components = new ArrayList<>();

    public Component(PDRectangle pdRectangle){
        this.pdRectangle = pdRectangle;
    }

    public void addComponent(Component component){
        if(components.stream().anyMatch(x->x.isOverlapped(component)))throw new RuntimeException("Component is overlapped with other Component");
        components.add(component);
    }

    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        final float lineWidth = 2f;
        pdPageContentStream.setLineWidth(lineWidth);
        pdPageContentStream.addRect(
                pdRectangle.getLowerLeftX()+lineWidth,
                pdRectangle.getLowerLeftY()+lineWidth,
                pdRectangle.getWidth()-2*lineWidth,
                pdRectangle.getHeight()-2*lineWidth);
        pdPageContentStream.stroke();

        for(Component component : components) component.render(pdPageContentStream);
    }

    public boolean isOverlapped(Component component){
        return pdRectangle.getLowerLeftX() < component.pdRectangle.getUpperRightX() &&
                pdRectangle.getUpperRightX() > component.pdRectangle.getLowerLeftX() &&
                pdRectangle.getLowerLeftY() < component.pdRectangle.getUpperRightY() &&
                pdRectangle.getUpperRightY() > pdRectangle.getLowerLeftY();
    }
}
