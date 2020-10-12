package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@EqualsAndHashCode(callSuper = true)
@Data
public class Header extends Component {
    public Header(PDRectangle pdRectangle) {
        super(pdRectangle);
    }

    public static Header voidHeader(Report report,float height) {
        return new Header(new PDRectangle(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getUpperRightY()-height,report.getMediaBoxPage().getWidth(),height));
    }
}
