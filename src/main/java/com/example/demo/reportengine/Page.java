package com.example.demo.reportengine;

import com.example.demo.exceptions.OverlappingException;
import com.example.demo.reportengine.components.UnevenTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@EqualsAndHashCode(callSuper = true)
@Data
public class Page extends Component {
    private Component header;
    private Component footer;
    public Page(PDRectangle pdRectangle, Component header, Component footer) throws OverlappingException, CloneNotSupportedException {
        super(pdRectangle);
        if (header!=null) {
            this.header = header.clone();//SerializationUtils.clone(header);//new UnevenTable((UnevenTable) header);
            addComponent(this.header);
        }
        if (footer!=null){
            this.footer = footer.clone();//SerializationUtils.clone(footer);//new UnevenTable((UnevenTable) footer);
            addComponent(this.footer);
        }
    }
}
