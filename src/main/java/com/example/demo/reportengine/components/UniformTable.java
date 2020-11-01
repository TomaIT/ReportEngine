package com.example.demo.reportengine.components;

import com.example.demo.exceptions.OverlappingException;
import com.example.demo.reportengine.Component;
import lombok.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@EqualsAndHashCode(callSuper = true)
@Data
public class UniformTable extends Component implements Cloneable {
    @Setter(AccessLevel.NONE)
    private static final float rowMinMargin = 10f;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    //First Row is considered Header
    private Component[][] table;

    public UniformTable(Component[][] table) {
        super();
        int firstRowColumn = table[0].length;
        for (int i=1;i<table.length;i++) if (table[i].length != firstRowColumn) throw new RuntimeException("Table isn't uniform");
        this.table = table;
        this.setSplittable(true);
    }
    public UniformTable(Component[][] table, Color borderColor, Color backgroundColor) {
        super(borderColor!=null,borderColor,backgroundColor!=null,backgroundColor);
        int firstRowColumn = table[0].length;
        for (int i=1;i<table.length;i++) if (table[i].length != firstRowColumn) throw new RuntimeException("Table isn't uniform");
        this.table = table;
        this.setSplittable(true);
    }


    /**
     * Costruisce i rettangoli per ogni cella.
     * Suppone una suddivisione delle colonne proporzionale alla larghezza minima delle celle.
     * Verifica che la cella cosi creata sia idonea a contenerne il contenuto, se cosi non è,
     * applica logiche di riduzione del componente per cercare di far stare il contenuto nello spazio (larghezza) dedicato.
     * VerticalAlign is top, se si vuole più flessibilità occorre un riposizionamento e l'aggiunta di un attributo di allineamento.
     * @param startX
     * @param endY
     * @param maxWidth
     * @return height
     */
    @Override
    public float buildNoMinHeight(float startX, float endY, float maxWidth, float topMargin) throws CloneNotSupportedException, OverlappingException {
        getComponents().clear();
        int i,j,k;
        float[] columnWidth = new float[0];

        for(k=0;k<3;k++) {
            columnWidth = getColumnWidths(maxWidth);
            for (i = 0; i < table.length; i++) {
                Component[] row = table[i];
                for (j = 0; j < row.length; j++) {
                    row[j].buildMaxWidth(columnWidth[j]);
                }
            }
            System.out.println(Arrays.toString(columnWidth));
        }

        //Aggiusto altezza celle e riallineo
        float sumHeight = topMargin;
        float tempEndY = endY - topMargin;

        for(i=0;i<table.length;i++){
            Component[] row = table[i];
            float rowHeight = getMinHeightRow(row);
            float offsetX = 0;
            for (j = 0; j < row.length; j++) {
                row[j].adjust(startX + offsetX,tempEndY,rowHeight,columnWidth[j]);
                offsetX += columnWidth[j];
            }
            tempEndY -= row[0].getPdRectangle().getHeight();
            sumHeight += row[0].getPdRectangle().getHeight();
        }

        setPdRectangle(new PDRectangle(startX,endY-sumHeight,maxWidth,sumHeight));
        Arrays.stream(table).forEach(x-> Arrays.stream(x).forEach(y-> getComponents().add(y)));
        return sumHeight;
    }

    private float[] getColumnWidths(float maxWidth) {
        float[] columnWidths = new float[table[0].length];
        float totalWidth = 0;
        float maximumWidth = (float) (1.3*maxWidth/table[0].length);
        for(int j=0;j<table[0].length;j++){
            float localMaxWidth = -1;
            for(int i=0;i<table.length;i++){
                if((table[i][j] instanceof TextCell)){
                    float temp = ((TextCell)table[i][j]).getMaxWidthSplitted();
                    if(localMaxWidth<temp)
                        localMaxWidth = temp;
                }else {
                    if(localMaxWidth<table[i][j].getMinWidth())
                        localMaxWidth = table[i][j].getMinWidth();
                }
            }
            if(localMaxWidth>maximumWidth) localMaxWidth = maximumWidth;
            columnWidths[j] = localMaxWidth;
            totalWidth += localMaxWidth;
        }

        for(int i=0;i<table[0].length;i++) columnWidths[i] = columnWidths[i] * maxWidth / totalWidth;

        return columnWidths;
    }

    @Override
    public float getMinHeight() {
        return (float) Arrays.stream(table).mapToDouble(this::getMinHeightRow).sum();
    }

    @Override
    protected void renderWithoutComponents(PDPageContentStream pdPageContentStream) throws IOException {
        if(!isVisible())return;
        super.renderWithoutComponents(pdPageContentStream);
    }

    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        if(!isVisible())return;
        renderWithoutComponents(pdPageContentStream);
        for(Component component : this.getComponents()) component.render(pdPageContentStream);
    }

    private float getMinHeightRow(Component[] row) {
        return (float) (Arrays.stream(row).mapToDouble(Component::getMinHeight).max().orElse(0)+rowMinMargin);
    }

    @Override
    public Component split(float firstHeight) throws CloneNotSupportedException, OverlappingException {
        List<Component[]> nextComponentTable = new ArrayList<>();
        List<Component[]> firstComponentTable = new ArrayList<>();
        Component[] header = table[0];
        Component[] headerClone = new Component[table[0].length];
        int i;

        float topMargin = getPdRectangle().getUpperRightY()-header[0].getPdRectangle().getUpperRightY();
        float height = header[0].getPdRectangle().getHeight()+topMargin;
        if(height>firstHeight) return null;

        for(i=0;i<header.length;i++) headerClone[i]=header[i].clone();

        for(i=1;i<table.length;i++){
            height+=table[i][0].getPdRectangle().getHeight();
            if(height>firstHeight)break;
            firstComponentTable.add(table[i]);
        }
        for(;i<table.length;i++){
            nextComponentTable.add(table[i]);
        }
        table = new Component[1+firstComponentTable.size()][header.length];
        table[0] = header;
        for(i=0;i<firstComponentTable.size();i++) table[i+1]=firstComponentTable.get(i);

        Component[][] nextTable = new Component[1+nextComponentTable.size()][header.length];
        nextTable[0] = headerClone;
        for(i=0;i<nextComponentTable.size();i++) nextTable[i+1]=nextComponentTable.get(i);

        buildNoMinHeight(getPdRectangle().getLowerLeftX(),getPdRectangle().getUpperRightY(),getPdRectangle().getWidth(),topMargin);

        UniformTable next = (UniformTable) super.clone();
        next.setPdRectangle(null);
        next.getComponents().clear();
        next.table = nextTable;
        return next;
    }

    @Override
    public UniformTable clone() throws CloneNotSupportedException {
        UniformTable ret = (UniformTable) super.clone();
        if(table!=null) {
            ret.table = new Component[table.length][];
            for(int i=0;i<table.length;i++){
                ret.table[i]=new Component[table[i].length];
                for(int j=0;j<table[i].length;j++){
                    ret.table[i][j] = table[i][j].clone();
                }
            }
        }
        return ret;
    }
}
