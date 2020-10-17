package com.example.demo.table;

import com.example.demo.Utility;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.Arrays;

@EqualsAndHashCode
@ToString
@Getter
@Builder
public class Table {
	private static final float heightFactor = 1.6f;
	private static final float cellMargin = 1f;
	// Table attributes
	private float margin;
	private PDRectangle pageSize;
	private boolean isLandscape;

	// font attributes
	private PDFont textFontHeader;
	private float fontSizeHeader;
	private PDFont textFontContent;
	private float fontSizeContent;

	// Content attributes
	private String[] headers;
	private String[][] content;

	private Float[] columnWidth;


	public Integer getNumberOfColumns() {
		return headers.length;
	}
	public Integer getNumberOfRows() {
		return content.length;
	}

	public float getCellMargin(){
		return cellMargin;
	}

	public float getColumnWidth(int col){
		Utility.startTimer("getColumnWidth");
		if(columnWidth == null) columnWidth=new Float[headers.length];
		if(columnWidth[col]==null) {
			double[] fact = new double[headers.length];
			double tot = 0.0;
			for (int i = 0; i < headers.length; i++) {
				fact[i] = getMinimumColumnWidth(i);
				tot += fact[i];
			}
			columnWidth[col] = (float) (getBoxWidth() * fact[col] / tot);
		}
		Utility.stopTimer("getColumnWidth");
		return columnWidth[col];
	}

	public float getRowHeight(){
		return getMinimumRowHeight()*heightFactor;
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
		return (float) (Math.min(fontSizeHeader,fontSizeContent)*0.01);
	}

	public boolean isBeautiful(){
		double tot = 0.0;
		for(int i=0;i<headers.length;i++) tot+=getMinimumColumnWidth(i);
		return tot<=getBoxWidth() && getMinimumRowHeight()<=getBoxHeight() ;
	}

	public boolean tryToBeauty(int maxIteration){
		float saveFontSizeHeader = fontSizeHeader;
		float saveFontSizeContent = fontSizeContent;
		for(int i=0;i<maxIteration && !isBeautiful() ;i++){
			fontSizeContent*=0.95;
			fontSizeHeader*=0.95;
			columnWidth = new Float[headers.length];
		}
		if(!isBeautiful()) {
			fontSizeHeader=saveFontSizeHeader;
			fontSizeContent=saveFontSizeContent;
		}
		return isBeautiful();
	}


	private float getMinimumColumnWidth(int col){
		float a = (float) Arrays.stream(content).mapToDouble(x-> {
			try {
				return Math.ceil(((double)textFontContent.getStringWidth(x[col])*fontSizeContent)/1000.0);
			} catch (Exception e) {
				return 1f;
			}
		}).max().orElse(1f);
		float b;
		try {
			b = (float) Math.ceil(((double)textFontHeader.getStringWidth(headers[col])*fontSizeHeader)/1000.0);
		} catch (Exception ignored) {
			b = 1f;
		}
		return Math.max(a, b)+(2*cellMargin);
	}

	private float getMinimumRowHeight(){
		float a;
		try {
			a = (float) Math.ceil(((double)textFontHeader.getFontDescriptor().getCapHeight() * fontSizeHeader) / 1000.0);
		}catch (Exception e){
			a = 1f;
		}
		float b;
		try {
			b = (float) Math.ceil(((double)textFontContent.getFontDescriptor().getCapHeight() * fontSizeContent) / 1000.0);
		}catch (Exception e){
			b = 1f;
		}
		return Math.max(a,b);
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
