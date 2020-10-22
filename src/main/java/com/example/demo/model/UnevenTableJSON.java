package com.example.demo.model;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.components.UnevenTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

@Data
@NoArgsConstructor
public class UnevenTableJSON {
    private List<Row> rows;

    public UnevenTable build(float startX,float endY,float maxWidth,float minHeight) {

        Component[][] components = new Component[rows.size()][];
        for(int i=0;i<rows.size();i++){
            Row row = rows.get(i);
            components[i]=new Component[row.getCols().size()];
            for(int j=0;j<row.getCols().size();j++){
                TextCellJSON textCellJSON = row.getCols().get(j);
                components[i][j]=textCellJSON.build();
            }
        }
        UnevenTable unevenTable = new UnevenTable(components, Color.RED,null);
        unevenTable.build(startX,endY,maxWidth,minHeight);
        return unevenTable;
    }
}

@Data
@NoArgsConstructor
class Row {
    private List<TextCellJSON> cols;
}
