package com.example.demo;

import com.example.demo.exceptions.OverlappingException;
import com.example.demo.model.ReportJSON;
import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import com.example.demo.reportengine.components.TextCell;
import com.example.demo.reportengine.components.UnevenTable;
import com.example.demo.reportengine.components.UniformTable;
import com.example.demo.reportengine.components.properties.HorizontalAlign;
import com.example.demo.reportengine.components.properties.VerticalAlign;
import com.example.demo.reportengine.services.FontService;
import com.example.demo.table.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication  implements CommandLineRunner {
    private final TemplateEngine templateEngine;
    private final FontService fontService;
    // Page configuration
    private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static final float MARGIN = 20;
    private static final boolean IS_LANDSCAPE = false;

    // Font configuration
    private static final PDFont TEXT_FONT = PDType1Font.HELVETICA_BOLD;
    private static final float FONT_SIZE = 20;
    private static final PDFont TEXT_FONT_CONTENT = PDType1Font.HELVETICA;
    private static final float FONT_SIZE_CONTENT = 20;

    // Table configuration
    private static final float ROW_HEIGHT = 15;
    private static final float CELL_MARGIN = 1;

    public DemoApplication(TemplateEngine templateEngine, FontService fontService) {
        this.templateEngine = templateEngine;
        this.fontService = fontService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private void testHtml() throws Exception{
        // Creo HTML - START
        final Context ctx = new Context();
        List<String> headers = Arrays.asList("ID", "Name", "Salary", "Status","Hello","Prova","Test","Troppe Colonneeee","Facciamolo andare oltreeee","CRASHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        List<Map<String, Object>> rows = new ArrayList<>();

        for(int i=0;i<100;i++)
            rows.add(Map.of(
                    "ID", String.valueOf(i),
                    "Name", "Jim",
                    "Salary", "50000",
                    "Status", "active",
                    "Hello","Mario",
                    "Prova","Luigi",
                    "Test","Daie",
                    "Troppe Colonneeee","Ciaoooone",
                    "Facciamolo andare oltreeee","AIUTOOOOOO",
                    "CRASHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH","CRASHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
            ));

        ctx.setVariable("headers", headers);
        ctx.setVariable("rows", rows);

        String htmlOutput = templateEngine.process("testWithHtml.html",ctx);
        // Creo HTML - END

        //System.out.println(htmlOutput);

        final Document document = Jsoup.parse(htmlOutput);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(document.html());
        renderer.layout();

        OutputStream os = Files.newOutputStream(Paths.get("./src/main/resources/html_"+System.currentTimeMillis()+".pdf"));
        renderer.createPDF(os);
    }

    private static Table createContent(int nRow,int nCol,int nChars) {
        String[][] content = new String[nRow][nCol];
        String[] headers = new String[nCol];
        headers[0]="#";
        for(int i=1;i<nCol;i++){
            headers[i]= RandomStringUtils.randomAlphabetic(1,nChars);
            for(int j=0;j<nRow;j++){
                content[j][0]=String.valueOf(j);
                content[j][i]=RandomStringUtils.randomAlphabetic(1,nChars);
            }
        }

        Table table = Table.builder()
                .headers(headers)
                .content(content)
                .margin(MARGIN)
                .pageSize(PAGE_SIZE)
                .isLandscape(IS_LANDSCAPE)
                .textFontHeader(TEXT_FONT)
                .fontSizeHeader(FONT_SIZE)
                .textFontContent(TEXT_FONT_CONTENT)
                .fontSizeContent(FONT_SIZE_CONTENT)
                .build();
        System.out.println(table.tryToBeauty(1000));
        return table;
    }

    private static TextCell createCell(HorizontalAlign align, String text, boolean underline) {
        //6218-5000
        return new TextCell(text,align, VerticalAlign.center,FontService.findFont("Arial"),12f,underline,null,Color.BLACK,null);

        /*TextCell textCell = new TextCell();
        textCell.setBackground(Color.CYAN);
        textCell.setColor(Utility.hex2Rgb("#000000"));
        textCell.setValue(text);
        textCell.setUnderline(underline);
        textCell.setHorizontalAlign(align);
        //cell.setVerticalAlign(VerticalAlign.bottom);
        textCell.setFontType(PDType1Font.COURIER);
        return textCell;*/
    }

    private static void prova() throws CloneNotSupportedException {
        final int nRow = 1000;
        final int nCol = 10;
        List<TextCell> cells = new ArrayList<>(nRow*nCol);
        long start = System.currentTimeMillis();
        for(int i=0;i<nRow;i++){
            for(int j=0;j<nCol;j++){
                cells.add(createCell(HorizontalAlign.center,
                        "A D I D A S prova ciao lalallalalallaalal" +
                                "alallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalalal allalalallaala",
                        true));
            }
        }
        for(TextCell cell : cells) {
            cell.build(0,0,25,250);
        }
        System.out.println(System.currentTimeMillis()-start);

    }

    private static Component[][] header(){
        Component[][] temp = new Component[2][];
        temp[0]=new Component[3];
        temp[1]=new Component[1];
        temp[0][0]=createCell(HorizontalAlign.left, "Continental", false);
        temp[0][1]=createCell(HorizontalAlign.center, "A D I D A S prova ciao lalallalalallaalal" +
                "alallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalalal allalalallaala", true);
        temp[0][2]=createCell(HorizontalAlign.right, "399$", false);
        temp[1][0]=createCell(HorizontalAlign.center, "oooooooooooo prova ciao", true);
        return temp;
    }
    private static Component[][] footer(){
        Component[][] temp = new Component[1][];
        temp[0]=new Component[1];
        temp[0][0]=createCell(HorizontalAlign.center, "FOOTER", false);
        return temp;
    }

    private static Component[][] table(int nRow,int nCol,int nChars) {
        Component[][] table = new Component[nRow][nCol];
        for(int i=0;i<nRow;i++){
            for(int j=0;j<nCol;j++){
                table[i][j]=createCell(HorizontalAlign.center,RandomStringUtils.randomAlphabetic(1,nChars),false);
            }
        }
        table[2][2] = createCell(HorizontalAlign.center,"Ma dai dici che davvero funziona? ullalala",true);
        return table;
    }

    private static void tryByReportJSON() throws IOException, OverlappingException, CloneNotSupportedException {
        ObjectMapper objectMapper = new ObjectMapper();
        ReportJSON reportJSON = objectMapper.readValue(new File("src/main/resources/report.json"), ReportJSON.class);
        System.out.println(reportJSON);
        Report report = reportJSON.build();
        report.render().save("src/main/resources/prove/byJSON_"+System.currentTimeMillis()+".pdf");
    }

    @Override
    public void run(String... args) throws Exception {
        /*Report report = new ObjectMapper().readValue(
                new File("./src/main/resources/report.json"),
                Report.class);*/

        //testPdfBox();
        //System.out.println("PDF-DONE");
        //testHtml();
        //System.out.println("HTML-DONE");

        Utility.startTimer("TOTAL");
        //tryByReportJSON();
        //new PDFTableGenerator().generatePDF(createContent(10,9,12),"src/main/resources/prove/tablePdfBox_"+System.currentTimeMillis()+".pdf");
        //prova();
        Report report = new Report(PDRectangle.A4,50,50,50,50,
                true,true,true,
                true,true,true);

        UnevenTable header = new UnevenTable(header(),Color.RED,null);
        //header.build(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getUpperRightY(),report.getMediaBoxPage().getWidth());
        report.setHeader(header);

        UnevenTable footer = new UnevenTable(footer(),Color.GREEN,null);
        //footer.build(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getLowerLeftY()+200f,report.getMediaBoxPage().getWidth());
        //footer.moveTo(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getLowerLeftY());
        report.setFooter(footer);

        report.addContent(new UniformTable(table(10,7,10),Color.CYAN,null));
        report.addContent(new UniformTable(table(15,4,10),Color.GREEN,null));
        report.addContent(new UniformTable(table(3,4,10),Color.ORANGE,null));
        report.addContent(new UniformTable(table(30,4,10),Color.GREEN,null));

        report.build();


        report.render().save("src/main/resources/prove/components_"+System.currentTimeMillis()+".pdf");

        //report.getPages().forEach(x->System.out.println(x.getFirstVoidSpace()));

        Utility.stopTimer("TOTAL");

        Utility.printTimers();
        System.out.println("DONE");
    }
}
