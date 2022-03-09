package visualize;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import storage.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

public class Reporter {
    public enum ReportType{
        STATEMENT_COVERAGE
    }
    private static final String titleKey="title";
    private static final Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public static void generateReport(String path, ReportType type, Map<String,String> parameters) throws DocumentException, FileNotFoundException {
        if(type==ReportType.STATEMENT_COVERAGE){
            generateStatementCoverageReport(path,parameters);
        }
    }

    private static void generateStatementCoverageReport(String path, Map<String,String> parameters) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        File f=new File(path);
        if(f.exists())f.delete();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        //add title
        if(!parameters.containsKey(titleKey)){
            parameters.put(titleKey,"Statement Coverage Report By Jacoconut");
        }
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(parameters.get(titleKey), bold));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(new Date().toString(),smallBold));
        addEmptyLine(preface, 1);
        document.add(preface);

        int linesSum=Storage.lines.get().values().stream().reduce(Integer::sum).get();
        int lineExec=Storage.exec_lines.get().values().stream().reduce(Integer::sum).get();
        Paragraph basicInfo = new Paragraph();
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph("Basic Coverage Info", bold));
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph(String.format("total_lines: %d\nexec_lines: %d\ncoverage_rate: %.3f%%\n",linesSum,lineExec, (double) 100*lineExec / (double) linesSum),smallBold));
        addEmptyLine(basicInfo, 3);
        document.add(basicInfo);

        //add table
        PdfPTable table = new PdfPTable(3);

        PdfPCell b = new PdfPCell(new Phrase("method_name"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("covered_lines/all_lines"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("coverage_rate(%)"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);
        table.setHeaderRows(1);

        for(String p:Storage.exec_lines.get().keySet()){
            table.addCell(p);
            table.addCell(String.format("%d/%d",Storage.exec_lines.get().get(p),Storage.lines.get().get(p)));
            table.addCell(String.valueOf((double) 100*Storage.exec_lines.get().get(p)/(double)Storage.lines.get().get(p)));
        }

        Paragraph content = new Paragraph();
        content.add(table);
        addEmptyLine(content,2);
        document.add(content);
        document.close();
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
