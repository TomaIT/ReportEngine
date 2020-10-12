package com.example.demo.reportengine.components;

import com.example.demo.Utility;
import com.example.demo.reportengine.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

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



    public Cell(PDRectangle pdRectangle, Color borderColor) {
        super(pdRectangle,borderColor);
    }
}
