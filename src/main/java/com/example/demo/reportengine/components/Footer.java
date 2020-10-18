package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Footer extends Component {
    public Footer(PDRectangle pdRectangle, Color borderColor) {
        super(pdRectangle,true,borderColor);
    }

    public static Footer voidFooter(Report report, float height, Color borderColor) {
        return new Footer(new PDRectangle(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getLowerLeftY(),report.getMediaBoxPage().getWidth(),height),borderColor);
    }

    public static Footer voidFooter(PDRectangle pdRectangle, float height, Color borderColor) {
        return new Footer(new PDRectangle(pdRectangle.getLowerLeftX(),pdRectangle.getLowerLeftY(),pdRectangle.getWidth(),height),borderColor);
    }
}
