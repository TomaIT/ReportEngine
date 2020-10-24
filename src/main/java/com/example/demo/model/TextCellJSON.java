package com.example.demo.model;

import com.example.demo.Utility;
import com.example.demo.reportengine.components.TextCell;
import com.example.demo.reportengine.components.properties.HorizontalAlign;
import com.example.demo.reportengine.components.properties.VerticalAlign;
import com.example.demo.reportengine.services.FontService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

@Data
@NoArgsConstructor
public class TextCellJSON {
    private String value;
    private String align;
    private String fontfamily;
    private String fontsize;
    private String color;
    private String background;
    private boolean underline;

    public TextCell build(){
        return new TextCell(
                value,
                getHorizontalAlign(),
                VerticalAlign.center,
                getPDFont(),
                getFontSize(),
                underline,
                Color.GREEN,
                getTextColor(),
                getBackgroundColor());
    }

    public HorizontalAlign getHorizontalAlign() {
        switch (align){
            case "left": return HorizontalAlign.left;
            case "right": return HorizontalAlign.right;
            case "center": return HorizontalAlign.center;
        }
        return HorizontalAlign.center;
    }

    public PDFont getPDFont() {
        if(fontfamily==null || fontfamily.isBlank()) return PDType1Font.HELVETICA;
        PDFont pdFont = FontService.findFont(fontfamily);
        //System.out.println(pdFont);
        return pdFont;
    }

    public float getFontSize() {
        if(fontsize == null || fontsize.isBlank()) return 15f;
        return Float.parseFloat(fontsize.replaceAll("px",""));
    }

    public Color getTextColor(){
        return Utility.hex2Rgb(color);
    }
    public Color getBackgroundColor(){
        return Utility.hex2Rgb(background);
    }

}
