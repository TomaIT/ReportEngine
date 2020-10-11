package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cell extends Component {
    private String value;
    private HorizontalAlign horizontalAlign;
    private VerticalAlign verticalAlign;
    private PDType1Font typeFont;
    private float fontSize;
    private boolean underline;
    private Color color;
    private Color background;



    public Cell(PDRectangle pdRectangle) {
        super(pdRectangle);
    }
}
