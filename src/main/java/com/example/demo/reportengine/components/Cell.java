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

    private void writeTextInRectangle(PDPageContentStream pdPageContentStream) throws IOException {
        final float minMargin = 2f;
        //TODO
        pdPageContentStream.setNonStrokingColor(color);
        pdPageContentStream.beginText();
        pdPageContentStream.setFont(fontType,fontSize);
        float x ;
        float y = getPdRectangle().getLowerLeftY();
        switch (horizontalAlign) {
            case left:  x = getPdRectangle().getLowerLeftX() + minMargin; break;
            case right: x = getPdRectangle().getUpperRightX() - width - minMargin; break;
            case center: x = getPdRectangle().getLowerLeftX() + (getPdRectangle().getWidth() - width) / 2.0f; break;
            default: x = getPdRectangle().getLowerLeftX() + minMargin;
        }
        switch (verticalAlign) {
            case top:  y = getPdRectangle().getUpperRightY()  - width - minMargin; break;
            case bottom: y = getPdRectangle().getLowerLeftY() + minMargin; break;
            case center: y = getPdRectangle().getLowerLeftY() + (getPdRectangle().getHeight() - height) / 2.0f; break;
            default: y = getPdRectangle().getLowerLeftY() + minMargin;
        }
        pdPageContentStream.newLineAtOffset(x,y);
        pdPageContentStream.showText(value);
        pdPageContentStream.endText();
    }

    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        final float lineWidth = 1.5f;



        //pdPageContentStream.setLineWidth(lineWidth);
        pdPageContentStream.setNonStrokingColor(background);
        pdPageContentStream.fillRect(getPdRectangle().getLowerLeftX(), getPdRectangle().getLowerLeftY(), getPdRectangle().getWidth(), getPdRectangle().getHeight());
        pdPageContentStream.stroke();
        writeTextInRectangle(pdPageContentStream);

        for(Component component : getComponents()) component.render(pdPageContentStream);

    }
    /** Justified TEXT
     * // Get the non-justified string width in text space units.
     *             float stringWidth = font.getStringWidth(message) * FONT_SIZE;
     *             // Get the string height in text space units.
     *             float stringHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() * FONT_SIZE;
     *             // Get the width we have to justify in.
     *             PDRectangle pageSize = page.getMediaBox();
     *             try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.OVERWRITE, false)) {
     *                 contentStream.beginText();
     *                 contentStream.setFont(font, FONT_SIZE);
     *                 // Start at top of page.
     *                 contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f));
     *                 // First show non-justified.
     *                 contentStream.showText(message);
     *                 // Move to next line.
     *                 contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 2));
     *                 // Now show word justified.
     *                 // The space we have to make up, in text space units.
     *                 float justifyWidth = pageSize.getWidth() * 1000f - stringWidth;
     *                 List<Object> text = new ArrayList<>();
     *                 String[] parts = message.split("\\s");
     *                 float spaceWidth = (justifyWidth / (parts.length - 1)) / FONT_SIZE;
     *                 for (int i = 0; i < parts.length; i++) {
     *                     if (i != 0) {
     *                         text.add(" ");
     *                         // Positive values move to the left, negative to the right.
     *                         text.add(-spaceWidth);
     *                     }
     *                     text.add(parts[i]);
     *                 }
     *                 contentStream.showTextWithPositioning(text.toArray());
     *                 contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 3));
     *                 // Now show letter justified.
     *                 text = new ArrayList<>();
     *                 justifyWidth = pageSize.getWidth() * 1000f - stringWidth;
     *                 float extraLetterWidth = (justifyWidth / (message.codePointCount(0, message.length()) - 1)) / FONT_SIZE;
     *                 for (int i = 0; i < message.length(); i += Character.charCount(message.codePointAt(i))) {
     *                     if (i != 0) {
     *                         text.add(-extraLetterWidth);
     *                     }
     *                     text.add(String.valueOf(Character.toChars(message.codePointAt(i))));
     *                 }
     *                 contentStream.showTextWithPositioning(text.toArray());
     *                 // PDF specification about word spacing:
     *                 // "Word spacing shall be applied to every occurrence of the single-byte character
     *                 // code 32 in a string when using a simple font or a composite font that defines
     *                 // code 32 as a single-byte code. It shall not apply to occurrences of the byte
     *                 // value 32 in multiple-byte codes.
     *                 // TrueType font with no word spacing
     *                 contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 4));
     *                 font = PDTrueTypeFont.load(doc, PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"),
     *                         WinAnsiEncoding.INSTANCE);
     *                 contentStream.setFont(font, FONT_SIZE);
     *                 contentStream.showText(message);
     *                 float wordSpacing = (pageSize.getWidth() * 1000f - stringWidth) / (parts.length - 1) / 1000;
     *                 // TrueType font with word spacing
     *                 contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 5));
     *                 font = PDTrueTypeFont.load(doc, PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"),
     *                         WinAnsiEncoding.INSTANCE);
     *                 contentStream.setFont(font, FONT_SIZE);
     *                 contentStream.setWordSpacing(wordSpacing);
     *                 contentStream.showText(message);
     *                 // Type0 font with word spacing that has no effect
     *                 contentStream.setTextMatrix(Matrix.getTranslateInstance(0, pageSize.getHeight() - stringHeight / 1000f * 6));
     *                 font = PDType0Font.load(doc, PDDocument.class.getResourceAsStream("/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"));
     *                 contentStream.setFont(font, FONT_SIZE);
     *                 contentStream.setWordSpacing(wordSpacing);
     *                 contentStream.showText(message);
     *                 // Finish up.
     *                 contentStream.endText();
     *
     */
}
