package com.example.demo.table;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.List;

@Data
@Builder
public class Table {
	// Table attributes
	private float margin;
	private float height;
	private PDRectangle pageSize;
	private boolean isLandscape;
	private float rowHeight;

	// font attributes
	private PDFont textFont;
	private float fontSize;

	// Content attributes
	private Integer numberOfRows;
	private List<Column> columns;
	private String[][] content;
	private float cellMargin;



	public Integer getNumberOfColumns() {
		return this.getColumns().size();
	}

	public float getWidth() {
		float tableWidth = 0f;
		for (Column column : columns) {
			tableWidth += column.getWidth();
		}
		return tableWidth;
	}

	public String[] getColumnsNamesAsArray() {
		String[] columnNames = new String[getNumberOfColumns()];
		for (int i = 0; i < getNumberOfColumns() - 1; i++) {
			columnNames[i] = columns.get(i).getName();
		}
		return columnNames;
	}

}
