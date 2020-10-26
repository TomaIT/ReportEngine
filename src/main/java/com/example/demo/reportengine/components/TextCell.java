package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.components.properties.HorizontalAlign;
import com.example.demo.reportengine.components.properties.VerticalAlign;
import com.example.demo.reportengine.services.FontService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TextCell extends Component implements Cloneable {
    @Setter(AccessLevel.NONE) private static final float minMarginText = 2f;
    @Setter(AccessLevel.NONE) private static final float underlineWidthFactor = 0.03f;
    @Setter(AccessLevel.NONE) private static final float underlineMarginFactor = 0.08f;
    private String value = "";
    private HorizontalAlign horizontalAlign = HorizontalAlign.center;
    private VerticalAlign verticalAlign = VerticalAlign.center;
    private PDFont fontType = PDType1Font.HELVETICA;
    private float fontSize = 12f;
    private boolean underline = false;
    private Color textColor = Color.BLACK;
    //@Setter(AccessLevel.NONE) private boolean textArea = false;

    public TextCell() {
        super();
        //updateHeights();
        //updateWidths();
    }
    public TextCell(String value, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign, PDFont fontType,
                    float fontSize, boolean underline, Color borderColor, Color textColor, Color backgroundColor) {
        super(borderColor!=null,borderColor,backgroundColor!=null,backgroundColor);
        this.value = value;
        this.horizontalAlign = horizontalAlign;
        this.verticalAlign = verticalAlign;
        this.fontType = fontType;
        this.fontSize = fontSize;
        this.underline = underline;
        this.textColor = textColor == null ? Color.BLACK : textColor;
        //updateHeights();
        //updateWidths();
    }

    @Override
    public float getMinHeight() {
        float textHeight = getTextHeight();
        /*if(textArea) {
            return (float) getComponents().stream().mapToDouble(Component::getMinHeight).sum();
        }*/
        return (underline) ?
                textHeight + minMarginText*2 + (fontSize*underlineWidthFactor) + (fontSize*underlineMarginFactor) :
                textHeight + minMarginText*2;
    }

    @Override
    public float getMinWidth() {
        float textWidth = getTextWidth();
        /*if(textArea) {
            return (float) getComponents().stream().mapToDouble(Component::getMinWidth).max().orElse(0);
        }*/
        return textWidth + minMarginText*2;
    }

    public float getTextHeight() {
        return (value == null || value.isBlank() /*|| textArea*/) ? 0 : FontService.getHeight(fontType,fontSize);
    }

    public float getTextWidth() {
        return  (value == null || value.isBlank() /*|| textArea*/) ? 0 : FontService.getWidth(value,fontType,fontSize);
    }

    /**
     *
     * @param startX
     * @param endY
     * @param maxWidth
     * @param minHeight
     * @return true se ha aumentato la height rispetto a minHeight, altrimenti false
     * NOTA: se true necessita ricalcolo se si vuole aggiungere margine
     */
    @Override
    public boolean build(float startX,float endY,float maxWidth,float minHeight) throws CloneNotSupportedException {
        if(minHeight < getMinHeight()) throw new RuntimeException("TextCell height isn't sufficient.");
        /*if (textArea) {// Ricalcolo dall'inizio
            textArea=false;
            setPdRectangle(null);
            getComponents().clear();
        }*/
        //String[] split = value.split("\\s+");
        if (getMinWidth() > maxWidth) {
            //if (split.length > 1) return buildTextArea(startX,endY,maxWidth,minHeight,split);
            setValue(shortens(value,maxWidth,fontType,fontSize));
        }
        setPdRectangle(new PDRectangle(startX,endY-minHeight,maxWidth,minHeight));
        return false;
    }

    @Override
    protected void renderWithoutComponents(PDPageContentStream pdPageContentStream) throws IOException {
        if(!isVisible())return;
        super.renderWithoutComponents(pdPageContentStream);
        if (value != null && !value.isBlank() /*&& !textArea*/) {
            pdPageContentStream.setNonStrokingColor(textColor);
            pdPageContentStream.beginText();
            pdPageContentStream.setFont(fontType,fontSize);
            float x ;
            float y ;
            float textWidth = getTextWidth();
            float textHeight = getTextHeight();
            switch (horizontalAlign) {
                case left: x = getPdRectangle().getLowerLeftX() + minMarginText; break;
                case right: x = getPdRectangle().getUpperRightX() - minMarginText - textWidth; break;
                case center: x = getPdRectangle().getLowerLeftX() + (getPdRectangle().getWidth() - textWidth) / 2.0f; break;
                default: throw new RuntimeException("Not yet implemented HorizontalAlign: "+horizontalAlign);
            }
            float underlineMargin = (underline) ? fontSize*underlineWidthFactor+fontSize*underlineMarginFactor : 0f;
            switch (verticalAlign) {
                case top:  y = getPdRectangle().getUpperRightY() - textHeight - minMarginText; break;
                case center: y = getPdRectangle().getLowerLeftY() + (getPdRectangle().getHeight() - textHeight - underlineMargin ) / 2.0f ; break;
                case bottom: y = getPdRectangle().getLowerLeftY() + minMarginText + underlineMargin; break;
                default: throw new RuntimeException("Not yet implemented VerticalAlign: "+verticalAlign);
            }
            pdPageContentStream.newLineAtOffset(x,y);
            pdPageContentStream.showText(value);

            pdPageContentStream.endText();

            if (underline) {
                pdPageContentStream.setLineWidth(fontSize*underlineWidthFactor);
                pdPageContentStream.moveTo(x, y-fontSize*underlineMarginFactor);
                pdPageContentStream.setStrokingColor(textColor);
                pdPageContentStream.lineTo(x + textWidth, y-fontSize*underlineMarginFactor);
                pdPageContentStream.stroke();
            }
        }
    }
    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        if(!isVisible())return;
        renderWithoutComponents(pdPageContentStream);
        for(Component component : getComponents()) component.render(pdPageContentStream);
    }

    @Override
    public TextCell clone() throws CloneNotSupportedException {
        TextCell textCell = (TextCell) super.clone();
        textCell.value = value;
        textCell.horizontalAlign = horizontalAlign;
        textCell.verticalAlign = verticalAlign;
        textCell.fontType = fontType; // TODO not sure
        textCell.fontSize = fontSize;
        textCell.underline = underline;
        textCell.textColor = new Color(textColor.getRGB());
        //textCell.textArea = textArea;
        //textCell.textHeight = textHeight;
        //textCell.textWidth = textWidth;
        //textCell.updateHeights();
        //textCell.updateWidths();
        return textCell;
    }

    private String shortens(String value,float maxWidth,PDFont fontType,float fontSize) { // TODO inefficient performance
        final int nCharsSubstitute = 3;
        while (FontService.getWidth(value,fontType,fontSize)+ minMarginText*2 > maxWidth) {
            value = value.substring(0, value.length() - nCharsSubstitute).replaceFirst(".{"+nCharsSubstitute+"}$", "...");
        }
        return value;
    }

    // TODO TextArea nearest
    /*private String[] buildStringsWithMaxWidth(String[] split,float maxWidth,PDFont fontType,float fontSize) {
        List<String> retValue = new ArrayList<>();
        for (int i=0;i<split.length;i++) {
            String value = split[i];
            if (FontService.getWidth(value,fontType,fontSize)+ minMarginText*2 > maxWidth) {
                value = shortens(value,maxWidth,fontType,fontSize);
            } else {
                for(int j=i+1;j<split.length;j++){
                    if(FontService.getWidth(value+" "+split[j],fontType,fontSize) + minMarginText*2 > maxWidth) break;
                    value += " "+split[j];
                    i++;
                }
            }
            retValue.add(value);
        }
        return retValue.toArray(new String[0]);
    }
    private boolean buildTextArea(float startX,float endY,float maxWidth,float minHeight,String[] split) throws CloneNotSupportedException {
        List<TextCell> cells = new ArrayList<>();
        String[] values = buildStringsWithMaxWidth(split,maxWidth,fontType,fontSize);
        float tempHeight = minHeight/values.length;
        float height = (getMinHeight()<tempHeight) ? tempHeight : getMinHeight();
        float totHeight = height * values.length;
        for (int i=0;i<values.length;i++) {
            TextCell temp = new TextCell(values[i],horizontalAlign,verticalAlign,fontType,fontSize, underline,
                    getBorderColor(), // TODO null
                    textColor,null);
            //Return always false
            temp.build(startX,endY-i*height,maxWidth,height);
            cells.add(temp);
        }

        setPdRectangle(new PDRectangle(startX,endY-totHeight,maxWidth,totHeight));
        getComponents().clear();
        cells.forEach(x->getComponents().add(x));
        textArea = true;
        //updateWidths();
        //updateHeights();
        //setValue("");
        //setUnderline(false);
        System.out.println(getMinHeight()+" - "+minHeight);
        return getMinHeight()>minHeight;// Return always true because is changed height (rowMargin of father component isn't considered)
    }*/
}
