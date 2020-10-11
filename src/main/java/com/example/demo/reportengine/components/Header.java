package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@EqualsAndHashCode(callSuper = true)
@Data
public class Header extends Component {
    public Header(PDRectangle pdRectangle) {
        super(pdRectangle);
    }
}
