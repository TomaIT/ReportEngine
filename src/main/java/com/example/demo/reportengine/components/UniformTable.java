package com.example.demo.reportengine.components;

import com.example.demo.reportengine.Component;
import lombok.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
public class UniformTable extends Component implements Cloneable {
    @Setter(AccessLevel.NONE)
    private static final float rowMinMargin = 5f;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Component[][] table;

    public UniformTable(Component[][] table) {
        super();
        int firstRowColumn = table[0].length;
        for (int i=1;i<table.length;i++) if (table[i].length != firstRowColumn) throw new RuntimeException("Table isn't uniform");
        this.table = table;
    }
    public UniformTable(Component[][] table, Color borderColor, Color backgroundColor) {
        super(borderColor!=null,borderColor,backgroundColor!=null,backgroundColor);
        int firstRowColumn = table[0].length;
        for (int i=1;i<table.length;i++) if (table[i].length != firstRowColumn) throw new RuntimeException("Table isn't uniform");
        this.table = table;
    }


    /**
     * Costruisce i rettangoli per ogni cella.
     * Suppone una suddivisione delle colonne proporzionale alla larghezza minima delle celle.
     * Verifica che la cella cosi creata sia idonea a contenerne il contenuto, se cosi non è,
     * applica logiche di riduzione del componente per cercare di far stare il contenuto nello spazio (larghezza) dedicato.
     * VerticalAlign is top, se si vuole più flessibilità occorre un riposizionamento e l'aggiunta di un attributo di allineamento.
     * TODO se minHeight è maggiore della height necessaria non aumenta la height ( da vedere se è necessario ), attualmente presenta bug
     * @param startX
     * @param endY
     * @param maxWidth
     * @param minHeight
     * @return true se ha aumentato la height rispetto a minHeight, altrimenti false
     */
    @Override
    public boolean build(float startX,float endY,float maxWidth,float minHeight) throws CloneNotSupportedException {
        float sumHeight = 0;
        float tempEndY = endY;
        int i,j;
        float[] columnWidth = getColumnWidths(maxWidth);
        for (i = 0; i < table.length; i++) {
            Component[] row = table[i];
            if (row.length <= 0) throw new RuntimeException("Cell row is void");

            float offsetX = 0;
            for (j = 0; j < row.length; j++) {
                boolean isChanged = row[j].build(
                        startX + offsetX,//j * columnWidth,
                        tempEndY,
                        columnWidth[j],
                        getMinHeightRow(row));
                offsetX += columnWidth[j];
                if (isChanged) {
                    j = -1;
                    offsetX = 0;
                    //columnWidth = getColumnWidths(maxWidth);
                }
            }
            tempEndY -= row[0].getPdRectangle().getHeight();
            sumHeight += row[0].getPdRectangle().getHeight();
        }
        /*// La dimensione altezza è minore a minHeight,
        // quindi applico un aumento proporzionato della height di ogni row
        if(sumHeight<minHeight) {
            System.out.println("aiaiaiiaia");
            float[] factors = new float[table.length];
            for(i=0;i<table.length;i++) factors[i] = table[i][0].getPdRectangle().getHeight() / sumHeight;
            tempEndY = endY;
            sumHeight =0;
            for(i=0;i<table.length;i++){
                Component[] row = table[i];
                if(row.length<=0) throw new RuntimeException("Cell row is void");
                //float[] columnWidth = getColumnWidths(maxWidth);
                float offsetX = 0;
                float rowHeight = minHeight*factors[i];
                System.out.println(rowHeight);
                for(j=0;j<row.length;j++) {
                    boolean isChanged = row[j].build(
                            startX + offsetX,//j * columnWidth,,
                            tempEndY,
                            columnWidth[j],
                            rowHeight);
                    offsetX += columnWidth[j];
                    if(isChanged){
                        j=-1;
                        offsetX = 0;
                        //columnWidth = getColumnWidths(maxWidth);
                    }
                }
                System.out.println(row[0].getPdRectangle().getHeight());
                System.out.println();
                tempEndY -= row[0].getPdRectangle().getHeight();
                sumHeight += row[0].getPdRectangle().getHeight();
            }
        }*/
        setPdRectangle(new PDRectangle(startX,endY-sumHeight,maxWidth,sumHeight));

        Arrays.stream(table).forEach(x-> Arrays.stream(x).forEach(y-> getComponents().add(y)));
        return minHeight < sumHeight;
    }

    private float[] getColumnWidths(float maxWidth) {
        float[] columnWidths = new float[table[0].length];
        float totalWidth = 0;
        for(int j=0;j<table[0].length;j++){
            float localMaxWidth = table[0][j].getMinWidth();
            for(int i=1;i<table.length;i++) if(localMaxWidth<table[i][j].getMinWidth()) localMaxWidth = table[i][j].getMinWidth();
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
