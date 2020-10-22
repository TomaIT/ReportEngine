package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@Data
@NoArgsConstructor
public class Metadata {
    private String format;
    private float marginleft;
    private float marginright;
    private float margintop;
    private float marginbottom;
    private boolean headerfirstpage;
    private boolean footerfirstpage;
    private boolean footercontentpage;
    private boolean headercontentpage;
    private boolean headerlastpage;
    private boolean footerlastpage;

    public PDRectangle getFormat() {
        switch (format) {
            case "DIN A0": return PDRectangle.A0;
            case "DIN A1": return PDRectangle.A1;
            case "DIN A2": return PDRectangle.A2;
            case "DIN A3": return PDRectangle.A3;
            case "DIN A4": return PDRectangle.A4;
            case "DIN A5": return PDRectangle.A5;
            case "DIN A6": return PDRectangle.A6;
            default: throw new RuntimeException("Format not implemented: "+format);
        }
    }
}
