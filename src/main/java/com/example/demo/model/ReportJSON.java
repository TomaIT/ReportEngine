package com.example.demo.model;

import com.example.demo.exceptions.OverlappingException;
import com.example.demo.reportengine.Report;
import com.example.demo.reportengine.components.UnevenTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportJSON {
    private UnevenTableJSON footer;
    private UnevenTableJSON header;
    private Metadata metadata;

    public Report build() throws OverlappingException {
        Report report = new Report(
                metadata.getFormat(),
                metadata.getMarginleft(),
                metadata.getMarginright(),
                metadata.getMargintop(),
                metadata.getMarginbottom());
        UnevenTable header = this.header.build(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getUpperRightY(),report.getMediaBoxPage().getWidth(),1f);
        UnevenTable footer = this.footer.build(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getLowerLeftY()+300f,report.getMediaBoxPage().getWidth(),1f);
        footer.build(report.getMediaBoxPage().getLowerLeftX(),report.getMediaBoxPage().getLowerLeftY()+footer.getPdRectangle().getHeight(),report.getMediaBoxPage().getWidth(),footer.getMinHeight());

        report.setHeader(header);
        report.setFooter(footer);
        report.addPage();


        return report;
    }
}
