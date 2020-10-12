package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Header extends Component {
    public Header(PDRectangle pdRectangle, Color borderColor) {
        super(pdRectangle,borderColor);
    }

    public static Header voidHeader(Report report,float height, Color borderColor) {
        return new Header(new PDRectangle(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getUpperRightY()-height,report.getMediaBoxPage().getWidth(),height),borderColor);
    }

    public static Header voidHeader(PDRectangle pdRectangle,float height, Color borderColor) {
        return new Header(new PDRectangle(pdRectangle.getLowerLeftX(),pdRectangle.getUpperRightY()-height,pdRectangle.getWidth(),height),borderColor);
    }
}
