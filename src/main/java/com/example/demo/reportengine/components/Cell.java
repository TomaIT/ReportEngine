package com.example.demo.reportengine.components;

import com.example.demo.Utility;
import com.example.demo.reportengine.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cell extends Component {
    private static final float minMarginText = 2f;
    private static final float underlineWidthFactor = 0.09f;
    private static final float underlineMarginFactor = 0.15f;
    private String value = "";
    private HorizontalAlign horizontalAlign = HorizontalAlign.left;
    private VerticalAlign verticalAlign = VerticalAlign.center;
    private PDType1Font fontType = PDType1Font.HELVETICA;
    private float fontSize = 12f;
    private boolean underline = false;
    private Color color = Color.BLACK;
    private Color background = Color.WHITE;
    private float minWidth;
    private float minHeight;

    public Cell() { updateMinHeight();updateMinWidth(); }
    public Cell(PDRectangle pdRectangle, Color borderColor) {
        super(pdRectangle,borderColor);
        updateMinHeight();
        updateMinWidth();
    }

    private void updateMinHeight() {
        minHeight = (underline) ?
                Utility.getHeight(fontType,fontSize) + minMarginText*2 + (fontSize*underlineWidthFactor) + (fontSize*underlineMarginFactor) :
                Utility.getHeight(fontType,fontSize) + minMarginText*2;
    }
    private void updateMinWidth() {
        minWidth = Utility.getWidth(value,fontType,fontSize)+ minMarginText*2;
    }


    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        updateMinWidth();
        updateMinHeight();
    }
    public void setValue(String value) {
        this.value = value;
        updateMinWidth();
    }
    public void setFontType(PDType1Font fontType) {
        this.fontType = fontType;
        updateMinWidth();
        updateMinHeight();
    }
    public void setUnderline(boolean underline) {
        this.underline = underline;
        updateMinHeight();
    }

    /** //TODO change anche height :D
     * Assume il testo in singola riga. Quindi se necessario riduce la fontSize, per farlo stare nello spazio dedicato.
     * @return true se ha eseguito modifiche alla fontSize (quindi height and width), altrimenti false
     */
    public boolean changeFontSizeToBeauty(){
        if (getPdRectangle().getWidth() < minWidth) {
            try {
                setFontSize((float) ((getPdRectangle().getWidth()-minMarginText*2)*1000.0/fontType.getStringWidth(value)));
                return true;
            } catch (Exception ignored) { }
        }
        return false;
    }

    private void writeTextInRectangle(PDPageContentStream pdPageContentStream) throws IOException {
        float textWidth = Utility.getWidth(value,fontType,fontSize);
        float textHeight = Utility.getHeight(fontType,fontSize);
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
