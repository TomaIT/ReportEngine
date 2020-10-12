package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@EqualsAndHashCode(callSuper = true)
@Data
public class Footer extends Component {
    public Footer(PDRectangle pdRectangle) {
        super(pdRectangle);
    }

    public static Footer voidFooter(Report report, float height) {
        return new Footer(new PDRectangle(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getLowerLeftY(),report.getMediaBoxPage().getWidth(),height));
    }
}
