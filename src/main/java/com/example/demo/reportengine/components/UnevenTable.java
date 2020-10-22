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
public class UnevenTable extends Component {
    @Setter(AccessLevel.NONE) private static final float rowMinMargin = 15f;
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.NONE) private Component[][] table;

    public UnevenTable(Component[][] table) {
        super();
        this.table = table;
    }

    public UnevenTable(Component[][] table, Color borderColor,Color backgroundColor) {
        super(borderColor!=null,borderColor,backgroundColor!=null,backgroundColor);
        this.table = table;
    }


    /**
     * Costruisce i rettangoli per ogni cella.
     * Suppone una suddivisione delle colonne equa. (es. 3 celle in una riga equivalgono a 3 colonne esattamente larghe uguali)
     * Verifica che la cella cosi creata sia idonea a contenerne il contenuto, se cosi non è,
     * applica logiche di riduzione del componente per cercare di far stare il contenuto nello spazio dedicato.
     * VerticalAlign is top, se si vuole più flessibilità occorre un riposizionamento e l'aggiunta di un attributo di allineamento.
     * @param startX
     * @param endY
     * @param maxWidth
     * @param minHeight
     * @return true se ha aumentato la height rispetto a minHeight, altrimenti false
     */
    @Override
    public boolean build(float startX,float endY,float maxWidth,float minHeight) {
        float sumHeight = 0;
        float tempEndY = endY;
        for(int i=0;i<table.length;i++){
            Component[] row = table[i];
            if (row.length <= 0) throw new RuntimeException("Cell row is void");
            float columnWidth = maxWidth / row.length;
            for (int j = 0; j < row.length; j++) {
                boolean isChanged = row[j].build(
                        startX + j * columnWidth,
                        endY - (float) Arrays.stream(table).limit(i).mapToDouble(this::getMinHeightRow).sum(),//tempEndY, //endY - (float) Arrays.stream(table).limit(i).mapToDouble(this::getMinHeightRow).sum(),
                        columnWidth,
                        getMinHeightRow(row));
                if (isChanged) {
                    j = -1;
                }
            }
            tempEndY -= row[0].getPdRectangle().getHeight();
            sumHeight += row[0].getPdRectangle().getHeight();
        }
        if(sumHeight<minHeight) {
            float[] factors = new float[table.length];
            for(int i=0;i<table.length;i++) factors[i] = table[i][0].getPdRectangle().getHeight() / sumHeight;
            tempEndY = endY;
            for(int i=0;i<table.length;i++){
                Component[] row = table[i];
                if(row.length<=0) throw new RuntimeException("Cell row is void");
                float columnWidth = maxWidth/row.length;
                for(int j=0;j<row.length;j++) {
                    boolean isChanged = row[j].build(
                            startX+j*columnWidth,
                            endY - (float) Arrays.stream(table).limit(i).mapToDouble(this::getMinHeightRow).sum(),//tempEndY,
                            columnWidth,
                            minHeight*factors[i]);
                    if(isChanged){
                        j=-1;
                    }
                }
                tempEndY -= row[0].getPdRectangle().getHeight();
                sumHeight += row[0].getPdRectangle().getHeight();
            }
        }
        setPdRectangle(new PDRectangle(startX,endY-sumHeight,maxWidth,sumHeight));

        Arrays.stream(table).forEach(x-> Arrays.stream(x).forEach(y-> getComponents().add(y)));
        return minHeight < sumHeight;
    }



    @Override
    public float getMinHeight() {
        return (float) Arrays.stream(table).mapToDouble(this::getMinHeightRow).sum();
    }

    @Override
    protected void renderWithoutComponents(PDPageContentStream pdPageContentStream) throws IOException {
        super.renderWithoutComponents(pdPageContentStream);
    }

    @Override
    public void render(PDPageContentStream pdPageContentStream) throws IOException {
        renderWithoutComponents(pdPageContentStream);
        for(Component component : this.getComponents()) component.render(pdPageContentStream);
    }

    private float getMinHeightRow(Component[] row) {
        return (float) (Arrays.stream(row).mapToDouble(Component::getMinHeight).max().orElse(0)+rowMinMargin);
    }
}
