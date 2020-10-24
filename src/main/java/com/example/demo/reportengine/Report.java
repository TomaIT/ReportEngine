package com.example.demo.reportengine;

import com.example.demo.exceptions.OverlappingException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Report {
    // Page attributes
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) private PDRectangle formatPage;
    private PDRectangle mediaBoxPage;
    private Component header = null;
    private Component footer = null;
    private boolean headerFirstPage;
    private boolean headerContentPage;
    private boolean headerLastPage;
    private boolean footerFirstPage;
    private boolean footerContentPage;
    private boolean footerLastPage;
    private List<Component> contents = new ArrayList<>();
    //Pages is builded by build() method
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) private List<Page> pages = new ArrayList<>();

    public Report(PDRectangle formatPage, float marginLeft, float marginRight, float marginTop, float marginBottom,
                  boolean headerFirstPage, boolean headerContentPage, boolean headerLastPage,
                  boolean footerFirstPage, boolean footerContentPage, boolean footerLastPage) {
        this.formatPage = formatPage;
        mediaBoxPage = new PDRectangle(formatPage.getLowerLeftX()+marginLeft,formatPage.getLowerLeftY()+marginBottom,formatPage.getWidth()-marginRight-marginLeft,formatPage.getHeight()-marginBottom-marginTop);
        this.headerFirstPage = headerFirstPage;
        this.headerContentPage = headerContentPage;
        this.headerLastPage = headerLastPage;
        this.footerFirstPage = footerFirstPage;
        this.footerContentPage = footerContentPage;
        this.footerLastPage = footerLastPage;
    }


    public void addContent(Component content) {
        contents.add(content);
    }

    /**
     * Method that makes the pages and split or refactor the components
     */
    public void build() throws OverlappingException, CloneNotSupportedException {
        pages.add(new Page(mediaBoxPage,header,footer));

        //TODO creazione dinamica :)

        PDRectangle voidSpace = pages.get(0).getFirstVoidSpace();
        contents.get(0).build(voidSpace.getLowerLeftX(),voidSpace.getUpperRightY()-10f,voidSpace.getWidth());
        pages.get(0).addComponent(contents.get(0));




        setHeaderAndFooterVisibilities();
    }

    private void setHeaderAndFooterVisibilities() {
        if(pages.size()>0){
            pages.get(0).getHeader().setVisible(headerFirstPage);
            pages.get(0).getFooter().setVisible(footerFirstPage);
        }
        if(pages.size()>1) {
            pages.get(pages.size()-1).getHeader().setVisible(headerLastPage);
            pages.get(pages.size()-1).getFooter().setVisible(footerLastPage);
        }
        for(int i=1;i<pages.size()-1;i++){
            pages.get(i).getHeader().setVisible(headerContentPage);
            pages.get(i).getFooter().setVisible(footerContentPage);
        }
    }

    /**
     * Render pages and components.
     * @return
     * @throws IOException
     */
    public PDDocument render() throws IOException {
        if(pages.size()<=0) throw new RuntimeException("Must be call build() before rendering. pages.size is 0.");
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
