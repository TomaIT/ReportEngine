package com.example.demo;

import com.example.demo.reportengine.Component;
import com.example.demo.table.PDFTableGenerator;
import com.example.demo.table.Table;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DemoApplicationTests {
    // Page configuration
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float MARGIN = 20;
    private static final boolean IS_LANDSCAPE = false;
    // Font configuration
    private static final PDFont TEXT_FONT = PDType1Font.HELVETICA_BOLD;
    private static final float FONT_SIZE = 20;
    private static final PDFont TEXT_FONT_CONTENT = PDType1Font.HELVETICA;
    private static final float FONT_SIZE_CONTENT = 20;


    private static Table createRandomTable(int nRow, int nCol, int nChars) {
        String[][] content = new String[nRow][nCol];
        String[] headers = new String[nCol];
        for(int i=0;i<nCol;i++){
            headers[i]= RandomStringUtils.randomAlphabetic(1,nChars);
            for(int j=0;j<nRow;j++){
                content[j][i]=RandomStringUtils.randomAlphabetic(1,nChars);
            }
        }

        Table table = Table.builder()
                .headers(headers)
                .content(content)
                .margin(MARGIN)
                .pageSize(PAGE_SIZE)
                .isLandscape(IS_LANDSCAPE)
                .textFontHeader(TEXT_FONT)
                .fontSizeHeader(FONT_SIZE)
                .textFontContent(TEXT_FONT_CONTENT)
                .fontSizeContent(FONT_SIZE_CONTENT)
                .build();
        System.out.println("Is Beautiful: "+table.tryToBeauty(1000));
        return table;
    }

    @Test
    void testTimingTableCreation() throws IOException {
        final int nRows = 1500;
        final int nCols = 30;
        final int nChars = 12;
        Utility.startTimer("(Rows: "+nRows+", nCols: "+nCols+", nChars: "+nChars+")");
        try (PDDocument doc = new PDDocument()) {
            PDFTableGenerator.drawTable(new PDDocument(),createRandomTable(nRows,nCols,nChars));
        }
        Utility.stopTimer("(Rows: "+nRows+", nCols: "+nCols+", nChars: "+nChars+")");
        Utility.printTimers();
    }

    @Test
    void testOverlapComponents() throws IOException {
        Component a = new Component(new PDRectangle(0,0,2,2), Color.BLACK);
        Component b = new Component(new PDRectangle(1,1,2,2), Color.BLACK);
        Component c = new Component(new PDRectangle(2,2,2,2), Color.BLACK);
        Component d = new Component(new PDRectangle(0,0,1,1), Color.BLACK);

        assertTrue(a.isOverlapped(b));assertTrue(b.isOverlapped(a));

        assertFalse(a.isOverlapped(c));assertFalse(c.isOverlapped(a));

        assertTrue(b.isOverlapped(c));assertTrue(c.isOverlapped(b));

        assertTrue(a.isOverlapped(d));assertTrue(d.isOverlapped(a));
        assertFalse(b.isOverlapped(d));assertFalse(d.isOverlapped(b));
        assertFalse(c.isOverlapped(d));assertFalse(d.isOverlapped(c));

    }

}
