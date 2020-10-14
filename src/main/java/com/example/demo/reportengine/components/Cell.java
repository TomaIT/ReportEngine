package com.example.demo.reportengine.components;

import com.example.demo.Utility;
import com.example.demo.reportengine.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cell extends Component {
    /**
     * TODO underline has influence in height calculation
     */
    private String value = "";
    private HorizontalAlign horizontalAlign = HorizontalAlign.left;
    private VerticalAlign verticalAlign = VerticalAlign.center;
    private PDType1Font fontType = PDType1Font.HELVETICA;
    private float fontSize = 12f;
    private boolean underline = false;
    private Color color = Color.BLACK;
    private Color background = Color.WHITE;
    private float width = Utility.getWidth(value,fontType,fontSize);
    private float height = Utility.getHeight(fontType,fontSize);

    public Cell() { }
    public Cell(PDRectangle pdRectangle, Color borderColor) {
        super(pdRectangle,borderColor);
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        width = Utility.getWidth(value,fontType,fontSize);
        height = Utility.getHeight(fontType,fontSize);
    }
    public void setValue(String value) {
        this.value = value;
        width = Utility.getWidth(value,fontType,fontSize);
    }
    public void setFontType(PDType1Font fontType) {
        this.fontType = fontType;
        width = Utility.getWidth(value,fontType,fontSize);
        height = Utility.getHeight(fontType,fontSize);
    }

    public boolean isBeautiful() {
        return getPdRectangle().getWidth()>=width && getPdRectangle().getHeight()>=height;
    }

    /**
     * // TODO Optimization to perfomance :D (not necessary iteration)
     * @param maxIteration
     * @return true se ha eseguito modifiche alla fontSize (quindi height and width), altrimenti false
     */
    public boolean changeFontSizeToBeauty(int maxIteration){
        if(isBeautiful())return false;
        float saveFontSize = fontSize;
        boolean changed = false;
        for (int i=0;i<maxIteration && !isBeautiful() ;i++) setFontSize((float) (fontSize*0.95));
        if (!isBeautiful()) {
            fontSize = saveFontSize;
            changed = false;
        }
        return changed;
    }

    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        final float lineWidth = 1.5f;
        //TODO
        pdPageContentStream.beginText();
        pdPageContentStream.setFont(fontType,fontSize);
        pdPageContentStream.moveTextPositionByAmount(getPdRectangle().getLowerLeftX(),getPdRectangle().getLowerLeftY());
        pdPageContentStream.drawString(value);
        pdPageContentStream.endText();

        pdPageContentStream.setLineWidth(lineWidth);
        pdPageContentStream.setStrokingColor(getBorderColor());
        pdPageContentStream.addRect(getPdRectangle().getLowerLeftX(), getPdRectangle().getLowerLeftY(), getPdRectangle().getWidth(), getPdRectangle().getHeight());
        pdPageContentStream.stroke();

        for(Component component : getComponents()) component.render(pdPageContentStream);

    }
}
