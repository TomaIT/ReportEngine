package com.example.demo.reportengine;

import com.example.demo.exceptions.OverlappingException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@EqualsAndHashCode(callSuper = true)
@Data
public class Page extends Component implements Cloneable {
    private Component header;
    private Component footer;
    private boolean full = false;

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

    @Override
    public Page clone() throws CloneNotSupportedException {
        Page ret = (Page) super.clone();
        if (header!=null) ret.header = header.clone();
        if (footer!=null) ret.footer = footer.clone();
        return ret;
    }
}
