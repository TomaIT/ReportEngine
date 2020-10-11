package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@EqualsAndHashCode(callSuper = true)
@Data
public class Footer extends Component {
    public Footer(PDRectangle pdRectangle) {
        super(pdRectangle);
    }
}
