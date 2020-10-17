package com.example.demo.reportengine.components;

import com.example.demo.Utility;
import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.components.properties.HorizontalAlign;
import com.example.demo.reportengine.components.properties.VerticalAlign;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextCell extends Component {
    @Setter(AccessLevel.NONE) private static final float minMarginText = 2f;
    @Setter(AccessLevel.NONE) private static final float underlineWidthFactor = 0.03f;
    @Setter(AccessLevel.NONE) private static final float underlineMarginFactor = 0.08f;
    private String value = "";
    private HorizontalAlign horizontalAlign = HorizontalAlign.left;
    private VerticalAlign verticalAlign = VerticalAlign.center;
    private PDType1Font fontType = PDType1Font.HELVETICA;
    private float fontSize = 12f;
    private boolean underline = false;
    private Color color = Color.BLACK;
    private Color background = Color.WHITE;
    @Setter(AccessLevel.NONE) private float minWidth;
    @Setter(AccessLevel.NONE) private float minHeight;
    @Setter(AccessLevel.NONE) private float textWidth;
    @Setter(AccessLevel.NONE) private float textHeight;

    public TextCell() {
        super();
        updateHeights();
        updateWidths();
    }

    private void updateHeights() {
        textHeight = Utility.getHeight(fontType,fontSize);
        minHeight = (underline) ?
                textHeight + minMarginText*2 + (fontSize*underlineWidthFactor) + (fontSize*underlineMarginFactor) :
                textHeight + minMarginText*2;
    }
    private void updateWidths() {
        textWidth = Utility.getWidth(value,fontType,fontSize);
        minWidth = textWidth + minMarginText*2;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        updateWidths();
        updateHeights();
    }
    public void setValue(String value) {
        this.value = value;
        updateWidths();
    }
    public void setFontType(PDType1Font fontType) {
        this.fontType = fontType;
        updateWidths();
        updateHeights();
    }
    public void setUnderline(boolean underline) {
        this.underline = underline;
        updateHeights();
    }

    @Override
    public void build(float startX,float startY,float maxWidth,float minHeight) {
        final int nCharsSubstitute = 3;
        if(minHeight < this.minHeight) throw new RuntimeException("TextCell height isn't sufficient.");

        while (minWidth > maxWidth) {
            setValue(value.substring(0, value.length() - nCharsSubstitute).replaceFirst(".{"+nCharsSubstitute+"}$", "..."));
        }

        setPdRectangle(new PDRectangle(startX,startY,maxWidth,minHeight));
    }

    private void writeTextInRectangle(PDPageContentStream pdPageContentStream) throws IOException {
        pdPageContentStream.setNonStrokingColor(color);
        pdPageContentStream.beginText();
        pdPageContentStream.setFont(fontType,fontSize);
        float x ;
        float y ;
        switch (horizontalAlign) {
            case right: x = getPdRectangle().getUpperRightX() - minMarginText - textWidth; break;
            case center: x = getPdRectangle().getLowerLeftX() + (getPdRectangle().getWidth() - textWidth) / 2.0f; break;
            default: x = getPdRectangle().getLowerLeftX() + minMarginText;
        }
        float underlineMargin = (underline) ? fontSize*underlineWidthFactor+fontSize*underlineMarginFactor : 0f;
        switch (verticalAlign) {
            case top:  y = getPdRectangle().getUpperRightY() - textHeight - minMarginText; break;
            case bottom: y = getPdRectangle().getLowerLeftY() + minMarginText + underlineMargin; break;
            default: y = getPdRectangle().getLowerLeftY() + (getPdRectangle().getHeight() - textHeight - underlineMargin ) / 2.0f ;
        }
        pdPageContentStream.newLineAtOffset(x,y);
        pdPageContentStream.showText(value);

        pdPageContentStream.endText();

        if (underline) {
            pdPageContentStream.setLineWidth(fontSize*underlineWidthFactor);
            pdPageContentStream.moveTo(x, y-fontSize*underlineMarginFactor);
            pdPageContentStream.setStrokingColor(color);
            pdPageContentStream.lineTo(x + textWidth, y-fontSize*underlineMarginFactor);
            pdPageContentStream.stroke();
        }
    }

    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        if(getPdRectangle()==null) throw new RuntimeException("Must be call build() before render.");
        final float lineWidth = 1.5f; pdPageContentStream.setLineWidth(lineWidth); // TODO remove
        //pdPageContentStream.setNonStrokingColor(background); // TODO add
        pdPageContentStream.setStrokingColor(background); // TODO remove
        pdPageContentStream.addRect(getPdRectangle().getLowerLeftX(), getPdRectangle().getLowerLeftY(), getPdRectangle().getWidth(), getPdRectangle().getHeight());
        //pdPageContentStream.fill(); // TODO add
        pdPageContentStream.stroke();
        writeTextInRectangle(pdPageContentStream);

        for(Component component : getComponents()) component.render(pdPageContentStream);

    }
}
