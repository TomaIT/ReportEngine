package com.example.demo.reportengine;

import com.example.demo.exceptions.OverlappingException;
import com.example.demo.reportengine.services.FontService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class Report {
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) private PDRectangle formatPage;
    @Setter(AccessLevel.NONE) private static final float minComponentMargin = 15f;
    @Setter(AccessLevel.NONE) private static final float minVoidSpace = minComponentMargin*2 + 50f;
    private PDRectangle mediaBoxPage;
    private Component header = null;
    private Component footer = null;
    private boolean headerFirstPage;
    private boolean headerContentPage;
    private boolean headerLastPage;
    private boolean footerFirstPage;
    private boolean footerContentPage;
    private boolean footerLastPage;
    private List<Component> contents = new LinkedList<>();
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
     * Method that makes the pages and split or refactor the components.
     */
    public void build() throws OverlappingException, CloneNotSupportedException {
        if (header!=null) header.buildNoMinHeight(mediaBoxPage.getLowerLeftX(),mediaBoxPage.getUpperRightY(),mediaBoxPage.getWidth(),0);
        if (footer!=null) {
            footer.buildNoMinHeight(mediaBoxPage.getLowerLeftX(),header.getPdRectangle().getLowerLeftY(),mediaBoxPage.getWidth(),0);
            footer.moveTo(mediaBoxPage.getLowerLeftX(),mediaBoxPage.getLowerLeftY());
        }

        pages.add(new Page(mediaBoxPage,header,footer));


        boolean loop = false;
        while (!contents.isEmpty()) {
            Component content = contents.get(0);
            boolean contentEntered = false;
            for(int i=0;i<pages.size();i++) {
                Page page = pages.get(i);
                if(page.isFull())continue;
                PDRectangle voidSpace = page.getFirstVoidSpace();
                if(voidSpace.getHeight() <= minVoidSpace) {
                    page.setFull(true);
                    continue;
                }

                float height = content.buildNoMinHeight(voidSpace.getLowerLeftX(),voidSpace.getUpperRightY(),voidSpace.getWidth(),minComponentMargin);
                if (height <= voidSpace.getHeight()) { // Perfetto il componente ci sta
                    page.addComponent(content);
                    contentEntered = true;
                    contents.remove(0);
                } else { // Il componente non ci sta nello spazio dedicato, bisogna applicare split se possibile oppure creiamo nuova pagina, se nella nuova pagina non ci sta crash
                    if (content.isSplittable()) {
                        Component nextComponent = content.split(voidSpace.getHeight());
                        if (nextComponent != null) { // Altrimenti non si può splittare con quella height minima, lo mettiamo nella pagina successiva.
                            //Only debug
                            if (content.getPdRectangle().getHeight() > height)
                                throw new RuntimeException("Debug error");

                            page.addComponent(content);
                            contentEntered = true;
                            contents.remove(0);
                            contents.add(0, nextComponent);
                        }
                    }


                    page.setFull(true);
                    //throw new RuntimeException("NOT IMPLEMENTED SPLIT");
                }
            }
            if(!contentEntered) { // C'è bisogno di una nuova pagina
                if(loop) throw new RuntimeException("Enter this component is impossible. "+content);
                pages.add(new Page(mediaBoxPage,header,footer));
                loop = true;
            } else {
                loop = false;
            }
        }




        setHeaderAndFooterVisibilities();
    }

    private void setHeaderAndFooterVisibilities() {
        if(header!=null) {
            if(pages.size()>0) pages.get(0).getHeader().setVisible(headerFirstPage);
            for(int i=1;i<pages.size()-1;i++) pages.get(i).getHeader().setVisible(headerContentPage);
            if(pages.size()>1) pages.get(pages.size()-1).getHeader().setVisible(headerLastPage);
        }
        if(footer!=null) {
            if(pages.size()>0) pages.get(0).getFooter().setVisible(footerFirstPage);
            for(int i=1;i<pages.size()-1;i++) pages.get(i).getFooter().setVisible(footerContentPage);
            if(pages.size()>1) pages.get(pages.size()-1).getFooter().setVisible(footerLastPage);
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
        for(Page componentPage : pages){
            PDPage page = new PDPage(formatPage);
            pdDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.OVERWRITE, false);
            componentPage.render(contentStream);
            contentStream.close();
        }
        FontService.reload();
        return pdDocument;
    }

}
