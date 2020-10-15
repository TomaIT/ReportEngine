package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class Header extends Component {
    private static final double rowHeightFactor = 3f;
    private List<List<Cell>> matrixValues = new ArrayList<>();

    public Header() { super(); }
    public Header(PDRectangle pdRectangle, Color borderColor) {
        super(pdRectangle,borderColor);
    }

    public void addCell(int nRow,Cell cell) {
        checkOverlapping(cell);
        while (matrixValues.size() <= nRow) matrixValues.add(new ArrayList<>());
        matrixValues.get(nRow).add(cell);
    }

    private float getMinHeightRow(List<Cell> row) {
        return (float) (row.stream().mapToDouble(Cell::getMinHeight).max().orElse(0)*rowHeightFactor);
    }

    public float getMinHeight() {
        return (float) matrixValues.stream().mapToDouble(this::getMinHeightRow).sum();
    }

    /**
     * Costruisce i rettangoli per ogni cella.
     * Suppone una suddivisione delle colonne equa. (es. 3 celle in una riga equivalgono a 3 colonne esattamente larghe uguali)
     * Verifica che la cella cosi creata sia idonea a contenerne il contenuto, se cosi non è,
     * applica logiche di riduzione del font per cercare di far stare il contenuto nello spazio dedicato.
     */
    public void build() {
        float sumHeight = 0;
        for(int i=0;i<matrixValues.size();i++) {
            List<Cell> row = matrixValues.get(i);
            if(row.size()<=0) throw new RuntimeException("Cell row is void");
            //float minHeight = getMinHeightRow(row);
            float columnWidth = getPdRectangle().getWidth()/row.size();
            for(int j=0;j<row.size();j++) {
                PDRectangle pdRectangle = new PDRectangle(
                        getPdRectangle().getLowerLeftX()+j*columnWidth,
                        getPdRectangle().getUpperRightY() - (float)matrixValues.stream().limit(i+1).mapToDouble(this::getMinHeightRow).sum(), // TODO perfomance improve
                        columnWidth,
                        getMinHeightRow(row)); // TODO performanceImprove
                row.get(j).setPdRectangle(pdRectangle);
                row.get(j).setBorderColor(Color.PINK);
                if (row.get(j).changeFontSizeToBeauty()) {
                    j = -1;
                }
            }
            sumHeight += row.get(0).getPdRectangle().getHeight();
        }
        PDRectangle old = getPdRectangle();
        setPdRectangle(new PDRectangle(old.getLowerLeftX(),old.getUpperRightY()-sumHeight,old.getWidth(),sumHeight));
    }

    @Override
    public void checkOverlapping(Component component){
        if(this.getComponents().stream().anyMatch(x->x.isOverlapped(component)) ||
                matrixValues.stream().anyMatch(x->x.stream().anyMatch(y->y.isOverlapped(component)))) {
            throw new RuntimeException("Component is overlapped with other Component");
        }
    }

    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        final float lineWidth = 1.5f;
        pdPageContentStream.setLineWidth(lineWidth);
        pdPageContentStream.setStrokingColor(this.getBorderColor());
        pdPageContentStream.addRect(this.getPdRectangle().getLowerLeftX(), this.getPdRectangle().getLowerLeftY(), this.getPdRectangle().getWidth(), this.getPdRectangle().getHeight());
        pdPageContentStream.stroke();

        for(Component component : this.getComponents()) component.render(pdPageContentStream);
        for(List<Cell> row : matrixValues) for(Cell c : row) c.render(pdPageContentStream);
    }




    public static Header voidHeader(Report report,float height, Color borderColor) {
        return new Header(new PDRectangle(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getUpperRightY()-height,report.getMediaBoxPage().getWidth(),height),borderColor);
    }
    public static Header voidHeader(PDRectangle pdRectangle,float height, Color borderColor) {
        return new Header(new PDRectangle(pdRectangle.getLowerLeftX(),pdRectangle.getUpperRightY()-height,pdRectangle.getWidth(),height),borderColor);
    }
}
