package com.example.demo;

import com.example.demo.model.Report;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication  implements CommandLineRunner {
    private final TemplateEngine templateEngine;

    public DemoApplication(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private void testHtml() throws Exception{
        // Creo HTML - START
        final Context ctx = new Context();
        List<String> headers = Arrays.asList("ID", "Name", "Salary", "Status");
        List<Map<String, Object>> rows = new ArrayList<>();

        for(int i=0;i<100;i++)
            rows.add(Map.of("ID", String.valueOf(i), "Name", "Jim", "Salary", "50000", "Status", "active"));

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

    private void testPdfBox() throws Exception{
        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage(PDRectangle.A4);
            doc.addPage(myPage);

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.beginText();

                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(14.5f);

                cont.newLineAtOffset(25, 700);
                String line1 = "World War II (often abbreviated to WWII or WW2), "
                        + "also known as the Second World War,";
                cont.showText(line1);

                cont.newLine();

                String line2 = "was a global war that lasted from 1939 to 1945, "
                        + "although related conflicts began earlier.";
                cont.showText(line2);
                cont.newLine();

                String line3 = "It involved the vast majority of the world's "
                        + "countries—including all of the great powers—";
                cont.showText(line3);
                cont.newLine();

                String line4 = "eventually forming two opposing military "
                        + "alliances: the Allies and the Axis.";
                cont.showText(line4);
                cont.newLine();

                cont.endText();
            }

            doc.save("src/main/resources/pdfBox"+System.currentTimeMillis()+".pdf");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        Report report = new ObjectMapper().readValue(
                new File("./src/main/resources/report.json"),
                Report.class);

        testPdfBox();
        System.out.println("PDF-DONE");
        testHtml();
        System.out.println("HTML-DONE");
    }
}
