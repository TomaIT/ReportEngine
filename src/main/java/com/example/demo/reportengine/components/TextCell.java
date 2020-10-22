package com.example.demo.reportengine.components;

import com.example.demo.Utility;
import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.services.FontService;
import com.example.demo.reportengine.components.properties.HorizontalAlign;
import com.example.demo.reportengine.components.properties.VerticalAlign;
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
public class TextCell extends Component {
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
    @Setter(AccessLevel.NONE) private float textWidth;
    @Setter(AccessLevel.NONE) private float textHeight;

    public TextCell() {
        super();
        updateHeights();
        updateWidths();
    }
    public TextCell(TextCell textCell) {
        super(textCell);
        setValue(textCell.value);
        setHorizontalAlign(textCell.horizontalAlign);
        setVerticalAlign(textCell.verticalAlign);
        setFontType(textCell.fontType);
        setFontSize(textCell.fontSize);
        setUnderline(textCell.underline);
        setTextColor(textCell.textColor);
        setBackgroundColor(textCell.getBackgroundColor());
        updateHeights();
        updateWidths();
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
        updateHeights();
        updateWidths();
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
    public void setFontType(PDFont fontType) {
        this.fontType = fontType;
        updateWidths();
        updateHeights();
    }
    public void setUnderline(boolean underline) {
        this.underline = underline;
        updateHeights();
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
    public boolean build(float startX,float endY,float maxWidth,float minHeight) {
        if(minHeight < this.minHeight) throw new RuntimeException("TextCell height isn't sufficient.");

        String[] split = value.split("\\s+");

        if (minWidth > maxWidth) {
            if (split.length > 1) return buildAreaText(startX,endY,maxWidth,minHeight,split);
            setValue(shortens(value,maxWidth,fontType,fontSize));
        }
        //se areaText necessita riposizionamento sulla y
        if(getComponents().size()>0) {
            float offset;
            boolean isAlreadyChanged;
            float minY = (float) getComponents().stream().mapToDouble(x->x.getPdRectangle().getLowerLeftY()).min().orElse(-1);
            float maxY = (float) getComponents().stream().mapToDouble(x->x.getPdRectangle().getUpperRightY()).max().orElse(-1);
            switch (verticalAlign) {
                case top:
                    offset = 0;
                    isAlreadyChanged = (maxY == endY);
                    break;
                case bottom:
                    offset = minHeight-this.minHeight;
                    isAlreadyChanged = (minY == endY-minHeight);
                    break;
                case center:
                    offset = (minHeight-this.minHeight) / 2;
                    isAlreadyChanged = (maxY == endY-offset);
                    break;
                default:
                    throw new RuntimeException("Not yet implemented VerticalAlign: "+verticalAlign);
            }
            //Prima verifico che il cambiamento non sia già stato fatto
            if (!isAlreadyChanged) {
                for (Component c : getComponents()) {
                    c.setPdRectangle(new PDRectangle(
                            c.getPdRectangle().getLowerLeftX(),
                            c.getPdRectangle().getLowerLeftY() - offset,
                            c.getPdRectangle().getWidth(),
                            c.getPdRectangle().getHeight()));
                }
            }
        }
        setPdRectangle(new PDRectangle(startX,endY-minHeight,maxWidth,minHeight));
        return false;
    }

    @Override
    protected void renderWithoutComponents(PDPageContentStream pdPageContentStream) throws IOException {
        super.renderWithoutComponents(pdPageContentStream);
        if (value != null && !value.isBlank()) {
            pdPageContentStream.setNonStrokingColor(textColor);
            pdPageContentStream.beginText();
            pdPageContentStream.setFont(fontType,fontSize);
            float x ;
            float y ;
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
        renderWithoutComponents(pdPageContentStream);
        for(Component component : getComponents()) component.render(pdPageContentStream);
    }


    private void updateHeights() {
        textHeight = (value == null || value.isBlank()) ? 0 : FontService.getHeight(fontType,fontSize);
        minHeight = (getPdRectangle() != null) ?
                getPdRectangle().getHeight() :
                (underline) ?
                        textHeight + minMarginText*2 + (fontSize*underlineWidthFactor) + (fontSize*underlineMarginFactor) :
                        textHeight + minMarginText*2;
    }
    private void updateWidths() {
        textWidth = (value == null || value.isBlank()) ? 0 : FontService.getWidth(value,fontType,fontSize);
        minWidth = (getPdRectangle() != null) ?
                getPdRectangle().getWidth() :
                textWidth + minMarginText*2;
    }

    private String shortens(String value,float maxWidth,PDFont fontType,float fontSize) { // TODO inefficient performance
        final int nCharsSubstitute = 3;
        while (FontService.getWidth(value,fontType,fontSize) > maxWidth) {
            value = value.substring(0, value.length() - nCharsSubstitute).replaceFirst(".{"+nCharsSubstitute+"}$", "...");
        }
        return value;
    }

    private String[] buildStringsWithMaxWidth(String[] split,float maxWidth,PDFont fontType,float fontSize) {
        List<String> retValue = new ArrayList<>();
        for (int i=0;i<split.length;i++) {
            String value = split[i];
            if (FontService.getWidth(value,fontType,fontSize) > maxWidth) {
                value = shortens(value,maxWidth,fontType,fontSize);
            } else {
                for(int j=i+1;j<split.length;j++){
                    if(FontService.getWidth(value+" "+split[j],fontType,fontSize) > maxWidth) break;
                    value += " "+split[j];
                    i++;
                }
            }
            retValue.add(value);
        }
        return retValue.toArray(new String[0]);
    }

    private boolean buildAreaText(float startX,float endY,float maxWidth,float minHeight,String[] split) {
        List<TextCell> cells = new ArrayList<>();
        String[] values = buildStringsWithMaxWidth(split,maxWidth,fontType,fontSize);
        float tempHeight = minHeight/values.length;
        float height = (this.minHeight<tempHeight) ? tempHeight : this.minHeight;
        float totHeight = height * values.length;
        for (int i=0;i<values.length;i++) {
            TextCell temp = new TextCell(this);
            temp.setValue(values[i]);
            //Return always false
            temp.build(startX,endY-i*height,maxWidth,height);
            cells.add(temp);
        }

        setPdRectangle(new PDRectangle(startX,endY-totHeight,maxWidth,totHeight));
        cells.forEach(x->getComponents().add(x));
        setValue("");
        setUnderline(false);
        return true;// Return always true because is changed height (rowMargin of father component isn't considered)
    }

}
