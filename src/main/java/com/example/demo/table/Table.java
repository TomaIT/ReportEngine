package com.example.demo.table;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Table {
	// Table attributes
	private float margin;
	private PDRectangle pageSize;
	private boolean isLandscape;

	// font attributes
	private PDFont textFont;
	private float fontSize;

	// Content attributes
	private String[] headers;
	private String[][] content;
	private float cellMargin;


	public Integer getNumberOfColumns() {
		return headers.length;
	}
	public Integer getNumberOfRows() {
		return content.length;
	}



	public float getColumnWidth(int col){
		double[] fact = new double[headers.length];
		double tot = 0.0;
		for(int i=0;i<headers.length;i++) {
			fact[i]=getMinimumColumnWidth(i);
			tot+=fact[i];
		}
		return (float) (getBoxWidth()*fact[col]/tot);
	}

	public float getRowHeight(){
		return getMinimumRowHeight()*1.6f;
	}

	public float getWidth() {
		float tableWidth = 0f;
		for (int i=0;i<headers.length;i++) {
			tableWidth += getColumnWidth(i);
		}
		return tableWidth;
	}

	public float getHeight(){
		try {
			return isLandscape ? pageSize.getWidth() - (2 * margin) : pageSize.getHeight() - (2 * margin);
		}catch (Exception e){
			return 1f;
		}
	}

	public float getLineSize(){
		return (float) (fontSize*0.01);
	}

	public boolean isBeautiful(){
		double tot = 0.0;
		for(int i=0;i<headers.length;i++) tot+=getMinimumColumnWidth(i);
		return tot<=getBoxWidth() && getMinimumRowHeight()<=getBoxHeight() ;
	}

	public boolean tryToBeauty(int maxIteration){
		float saveFontSize = fontSize;
		for(int i=0;i<maxIteration && !isBeautiful() ;i++){
			fontSize*=0.95;
		}
		if(!isBeautiful())fontSize=saveFontSize;
		return isBeautiful();
	}


	private float getMinimumColumnWidth(int col){
		float a = (float) Arrays.stream(content).mapToDouble(x-> {
			try {
				return Math.ceil(((double)textFont.getStringWidth(x[col])*fontSize)/1000.0);
			} catch (Exception e) {
				return 1f;
			}
		}).max().orElse(1f);
		float b;
		try {
			b = (float) Math.ceil(((double)textFont.getStringWidth(headers[col])*fontSize)/1000.0);
		} catch (Exception ignored) {
			b = 1f;
		}
		return Math.max(a, b)+(2*cellMargin);
	}

	private float getMinimumRowHeight(){
		try {
			return (float) Math.ceil(((double)textFont.getFontDescriptor().getCapHeight() * fontSize) / 1000.0);
		}catch (Exception e){
			return 1f;
		}
	}

	private float getBoxWidth(){
		try {
			return isLandscape ? pageSize.getHeight() - (2 * margin) : pageSize.getWidth() - (2 * margin);
		}catch (Exception e){
			return 1f;
		}
	}

	private float getBoxHeight(){
		try {
			return isLandscape ? pageSize.getWidth() - (2 * margin) : pageSize.getHeight() - (2 * margin);
		}catch (Exception e){
			return 1f;
		}
	}


}
