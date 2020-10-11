package com.example.demo.reportengine;

import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Report {
    // Page attributes
    private PDRectangle formatPage;
    private PDRectangle mediaBoxPage;
    private List<Component> pages = new ArrayList<>();

    public Report(PDRectangle formatPage, float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.formatPage = formatPage;
        mediaBoxPage = new PDRectangle(formatPage.getLowerLeftX()+marginLeft,formatPage.getLowerLeftY()+marginBottom,formatPage.getWidth()-marginRight-marginLeft,formatPage.getHeight()-marginBottom-marginTop);
        pages.add(new Component(mediaBoxPage));
    }


    public void addPage(){
        pages.add(new Component(mediaBoxPage));
    }

    /**
     * Render pages and components.
     * @return
     * @throws IOException
     */
    public PDDocument render() throws IOException {
        PDDocument pdDocument = new PDDocument();
        for(Component component : pages){
            PDPage page = new PDPage(formatPage);
            pdDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.OVERWRITE, false);
            component.drawRect(contentStream, Color.BLACK,false);
            contentStream.close();
        }
        return pdDocument;
    }



}
