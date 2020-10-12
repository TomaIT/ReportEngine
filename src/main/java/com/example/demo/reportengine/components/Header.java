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
    private static final double rowHeightFactor = 1.6f;
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

    public float getMinHeight() {
        return (float) matrixValues.stream()
                .mapToDouble(x->x.stream().mapToDouble(Cell::getHeight).max().orElse(0)*rowHeightFactor)
                .sum();
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
    }




    public static Header voidHeader(Report report,float height, Color borderColor) {
        return new Header(new PDRectangle(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getUpperRightY()-height,report.getMediaBoxPage().getWidth(),height),borderColor);
    }
    public static Header voidHeader(PDRectangle pdRectangle,float height, Color borderColor) {
        return new Header(new PDRectangle(pdRectangle.getLowerLeftX(),pdRectangle.getUpperRightY()-height,pdRectangle.getWidth(),height),borderColor);
    }
}
