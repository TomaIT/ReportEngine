package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Column {
    private String headervalue;
    private String value;
    private String align;
    private String fontfamily;
    private String fontsize;
    private String color;
    private String background;
    private boolean underline;
}
