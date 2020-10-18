package com.example.demo.reportengine;

import com.example.demo.exceptions.OverlappingException;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Report {
    // Page attributes
    private PDRectangle formatPage;
    private PDRectangle mediaBoxPage;
    private Component header = null;
    private boolean headerInAllPages = false;
    private Component footer = null;
    private boolean footerInAllPages = false;
    private List<Component> pages = new ArrayList<>();

    public Report(PDRectangle formatPage, float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.formatPage = formatPage;
        mediaBoxPage = new PDRectangle(formatPage.getLowerLeftX()+marginLeft,formatPage.getLowerLeftY()+marginBottom,formatPage.getWidth()-marginRight-marginLeft,formatPage.getHeight()-marginBottom-marginTop);
    }

    public void setHeader(Component header) {
        this.header = header;
    }
    public void setHeaderInAllPages(Component header) {
        this.header = header;
        headerInAllPages = true;
    }

    public void setFooter(Component footer) {
        this.footer = footer;
    }
    public void setFooterInAllPages(Component footer) {
        this.footer = footer;
        footerInAllPages = true;
    }


    public void addPage() throws OverlappingException {
        if(header == null || footer == null) throw new RuntimeException("Please before call addPage() set header & footer");
        Component page = new Component(mediaBoxPage);
        if(headerInAllPages || pages.size()<=0) page.addComponent(header);
        if(footerInAllPages || pages.size()<=0) page.addComponent(footer);
        pages.add(page);
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
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.OVERWRITE, true);
            component.render(contentStream);
            contentStream.close();
        }
        return pdDocument;
    }



}
