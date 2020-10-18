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

    public PDRectangle getFormat() {
        switch (format) {
            case "DIN A4": return PDRectangle.A4;
        }
        return null;
    }
}
