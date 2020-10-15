package com.example.demo;

import com.example.demo.reportengine.Component;
import com.example.demo.reportengine.Report;
import com.example.demo.reportengine.components.*;
import com.example.demo.table.PDFTableGenerator;
import com.example.demo.table.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

@SpringBootApplication
public class DemoApplication  implements CommandLineRunner {
    private final TemplateEngine templateEngine;
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

    public DemoApplication(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
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

    private static Cell createCell(HorizontalAlign align,String text,boolean underline) {
        Cell cell = new Cell();
        cell.setBackground(Color.CYAN);
        cell.setColor(Utility.hex2Rgb("#000000"));
        cell.setValue(text);
        cell.setUnderline(underline);
        cell.setHorizontalAlign(align);
        //cell.setVerticalAlign(VerticalAlign.bottom);
        cell.setFontType(PDType1Font.COURIER);
        return cell;
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
        //new PDFTableGenerator().generatePDF(createContent(10,9,12),"src/main/resources/prove/tablePdfBox_"+System.currentTimeMillis()+".pdf");

        Report report = new Report(PDRectangle.A4,25,25,25,25);

        Header header = Header.voidHeader(report,25f,Color.GRAY);
        for(int i=0;i<18;i+=2) {
            header.addCell(i, createCell(HorizontalAlign.left, "Continental prova ciao lalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaala", false));
            header.addCell(i, createCell(HorizontalAlign.center, "ADIDAS prova ciao lalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaala", true));
            header.addCell(i, createCell(HorizontalAlign.right, "399$ prova ciao lalallalalalalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalalalallalalallaalallaala", false));
            header.addCell(i+1, createCell(HorizontalAlign.center, "oooooooooooo prova ciao", true));
        }

        header.build();

        report.setFooter(Footer.voidFooter(report,3f,Utility.hex2Rgb("#fe9200")));
        report.setHeaderInAllPages(header);//Header.voidHeader(report,50f,Color.RED));

        report.addPage(Color.BLACK);
        report.getPages().get(0).addComponent(new Component(new PDRectangle(25,250,report.getMediaBoxPage().getWidth(),11.3f),Color.CYAN));
        report.getPages().get(0).addComponent(new Component(report.getPages().get(0).getFirstVoidSpace(),Color.GREEN));
        report.getPages().get(0).addComponent(new Component(report.getPages().get(0).getFirstVoidSpace(),Color.MAGENTA));
        report.addPage(Color.BLACK);

        report.getPages().get(1).addComponent(new Component(report.getPages().get(1).getFirstVoidSpace(),Color.GREEN));
        report.addPage(Color.BLACK);
        report.render().save("src/main/resources/prove/components_"+System.currentTimeMillis()+".pdf");

        report.getPages().forEach(x->System.out.println(x.getFirstVoidSpace()));

        Utility.stopTimer("TOTAL");

        Utility.printTimers();
        System.out.println("DONE");
    }
}
