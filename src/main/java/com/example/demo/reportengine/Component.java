package com.example.demo.reportengine;

import com.example.demo.exceptions.OverlappingException;
import lombok.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Component {
    @Setter(AccessLevel.NONE) private static final float borderLineWidth = 0.5f;
    private PDRectangle pdRectangle = null;
    private boolean bordered = false;
    private Color borderColor = Color.BLACK;
    private boolean filled = false;
    private Color backgroundColor = Color.WHITE;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<Component> components = new ArrayList<>();
    @Setter(AccessLevel.NONE) protected float minWidth;
    @Setter(AccessLevel.NONE) protected float minHeight;

    public Component(Component component) {
        if(component.pdRectangle != null) {
            this.pdRectangle = new PDRectangle(
                    component.pdRectangle.getLowerLeftX(),
                    component.pdRectangle.getLowerLeftY(),
                    component.pdRectangle.getWidth(),
                    component.pdRectangle.getHeight());
        }
        this.bordered = component.bordered;
        this.borderColor = component.borderColor;
        this.filled = component.filled;
        this.backgroundColor = component.backgroundColor;
        this.minWidth = component.minWidth;
        this.minHeight = component.minHeight;
        this.components = component.components.stream().map(Component::new).collect(Collectors.toList());
    }
    public Component(float startX,float startY,float width,float height){
        this.pdRectangle = new PDRectangle(startX,startY,width,height);
    }
    public Component(PDRectangle pdRectangle){
        this.pdRectangle = pdRectangle;
    }
    public Component(PDRectangle pdRectangle, boolean bordered, Color borderColor){
        this.pdRectangle = pdRectangle;
        this.bordered = bordered;
        this.borderColor = borderColor;
    }
    public Component(PDRectangle pdRectangle, boolean bordered, Color borderColor,boolean filled,Color backgroundColor){
        this.pdRectangle = pdRectangle;
        this.bordered = bordered;
        this.borderColor = borderColor;
        this.filled = filled;
        this.backgroundColor = backgroundColor;
    }
    public Component(boolean bordered, Color borderColor,boolean filled,Color backgroundColor){
        this.bordered = bordered;
        this.borderColor = borderColor;
        this.filled = filled;
        this.backgroundColor = backgroundColor;
    }


    public void addComponent(Component component) throws OverlappingException {
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

    protected List<Component> getComponents(){return components;}

    protected void renderWithoutComponents(PDPageContentStream pdPageContentStream) throws IOException {
        if (pdRectangle==null) throw new RuntimeException("Must be call build() before render.");
        if (filled || bordered) {
            if (bordered) {
                pdPageContentStream.setLineWidth(borderLineWidth);
                pdPageContentStream.setStrokingColor(borderColor);
            }
            if (filled) pdPageContentStream.setNonStrokingColor(backgroundColor);
            pdPageContentStream.addRect(pdRectangle.getLowerLeftX(), pdRectangle.getLowerLeftY(), pdRectangle.getWidth(), pdRectangle.getHeight());
            if (filled && bordered) pdPageContentStream.fillAndStroke();
            else if(filled) pdPageContentStream.fill();
            else pdPageContentStream.stroke();
        }
    }

    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        renderWithoutComponents(pdPageContentStream);
        for(Component component : components) component.render(pdPageContentStream);
    }

    /**
     *
     * @param startX
     * @param endY
     * @param maxWidth
     * @param minHeight
     * @return true se ha aumentato la height rispetto a minHeight, altrimenti false
     */
    public boolean build(float startX, float endY,float maxWidth,float minHeight){
        throw new RuntimeException("Not Implemented");
    }


    public boolean isOverlapped(Component component){
        return component != null && component.pdRectangle != null && pdRectangle != null &&
                component.pdRectangle.getUpperRightX() > pdRectangle.getLowerLeftX() &&
                pdRectangle.getUpperRightX() > component.pdRectangle.getLowerLeftX() &&
                component.pdRectangle.getUpperRightY() > pdRectangle.getLowerLeftY() &&
                pdRectangle.getUpperRightY() > component.pdRectangle.getLowerLeftY();
    }

    public void checkOverlapping(Component component) throws OverlappingException {
        if(components.stream().anyMatch(x->x.isOverlapped(component)))
            throw new OverlappingException("Component is overlapped with other Component");
    }
}
