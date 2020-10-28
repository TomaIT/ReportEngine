package com.example.demo.reportengine.components;

import com.example.demo.exceptions.OverlappingException;
import com.example.demo.reportengine.Component;
import lombok.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnevenTable extends Component implements Cloneable {
    @Setter(AccessLevel.NONE)
    private static final float rowMinMargin = 15f;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Component[][] table;

    public UnevenTable(Component[][] table) {
        super();
        this.table = table;
    }
    public UnevenTable(Component[][] table, Color borderColor, Color backgroundColor) {
        super(borderColor!=null,borderColor,backgroundColor!=null,backgroundColor);
        this.table = table;
    }


    /**
     * Costruisce i rettangoli per ogni cella.
     * Suppone una suddivisione delle colonne equa. (es. 3 celle in una riga equivalgono a 3 colonne esattamente larghe uguali)
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

        //Costruisco celle
        for(int i=0;i<table.length;i++){
            Component[] row = table[i];
            float columnWidth = maxWidth / row.length;
            for (int j = 0; j < row.length; j++) row[j].buildMaxWidth(columnWidth);
        }

        //Aggiusto altezza celle e riallineo
        float sumHeight = topMargin;
        float tempEndY = endY - topMargin;
        for(int i=0;i<table.length;i++){
            Component[] row = table[i];
            float columnWidth = maxWidth / row.length;
            float rowHeight = getMinHeightRow(row);
            for (int j = 0; j < row.length; j++) {
                row[j].adjust(startX + j * columnWidth,tempEndY,rowHeight,columnWidth);

            }
            tempEndY -= row[0].getPdRectangle().getHeight();
            sumHeight += row[0].getPdRectangle().getHeight();
        }
        setPdRectangle(new PDRectangle(startX,endY-sumHeight,maxWidth,sumHeight));
        Arrays.stream(table).forEach(x-> Arrays.stream(x).forEach(y-> getComponents().add(y)));
        return sumHeight;

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
    public UnevenTable clone() throws CloneNotSupportedException {
        UnevenTable ret = (UnevenTable) super.clone();
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
