package com.example.demo.table;

import lombok.Data;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Data
public class Cell {
	private String value;
	private PDFont textFont = PDType1Font.HELVETICA;
	private float fontSize = 10;

	public float getMinimumWidth() {
		try {
			return (float) Math.ceil(((double)textFont.getStringWidth(value)*fontSize)/1000.0);
		} catch (Exception e) {
			return 0f;
		}
	}

	public float getMinimumHeight() {
		try {
			return (float) Math.ceil(((double)textFont.getFontDescriptor().getCapHeight() * fontSize) / 1000.0);
		}catch (Exception e){
			return 0f;
		}
	}

}
