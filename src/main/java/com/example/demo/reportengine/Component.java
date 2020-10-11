package com.example.demo.reportengine;

import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
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

    public void drawRect(PDPageContentStream content, Color color, boolean fill) throws IOException {
        content.addRect(pdRectangle.getLowerLeftX()+1, pdRectangle.getLowerLeftY()+1, pdRectangle.getWidth()-2, pdRectangle.getHeight()-2);
        if (fill) {
            content.setNonStrokingColor(color);
            content.fill();
        } else {
            content.setStrokingColor(color);
            content.stroke();
        }
        for(Component component : components) {
            component.drawRect(content,color,fill);
        }
    }
}
