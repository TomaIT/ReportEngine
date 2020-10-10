package com.example.demo;

import com.example.demo.model.Report;
import com.example.demo.table.Column;
import com.example.demo.table.PDFTableGenerator;
import com.example.demo.table.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
        for(int i=0;i<nCol;i++){
            headers[i]= RandomStringUtils.randomAlphabetic(1,nChars);
            for(int j=0;j<nRow;j++){
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

    @Override
    public void run(String... args) throws Exception {
        Report report = new ObjectMapper().readValue(
                new File("./src/main/resources/report.json"),
                Report.class);

        //testPdfBox();
        //System.out.println("PDF-DONE");
        //testHtml();
        //System.out.println("HTML-DONE");

        //Utility.startTimer("TOTAL");
        //new PDFTableGenerator().generatePDF(createContent(50000,25,12),"src/main/resources/prove/tablePdfBox_"+System.currentTimeMillis()+".pdf");
        //Utility.stopTimer("TOTAL");

        //Utility.printTimers();
        System.out.println("DONE");
    }
}
