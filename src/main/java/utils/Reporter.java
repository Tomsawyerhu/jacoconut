package utils;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Reporter {
    public enum ReportType{
        STATEMENT_COVERAGE,
        BRANCH_COVERAGE,
        METHOD_COVERAGE
    }
    private static final String titleKey="title";
    private static final Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    public static void generateReport(String path, ReportType type, Map<String,String> parameters) throws DocumentException, FileNotFoundException {
        String p=path;
        if(!path.endsWith(".pdf")) p+=".pdf";
        if(type==ReportType.STATEMENT_COVERAGE){
            generateStatementCoverageReport(p,parameters);
        }else if(type==ReportType.BRANCH_COVERAGE){
            generateBranchCoverageReport(p,parameters);
        }else if(type==ReportType.METHOD_COVERAGE){
            generateMethodCoverageReport(p,parameters);
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

        int linesSum=Storage.lines.get().values().stream().mapToInt(Set::size).reduce(Integer::sum).getAsInt();
        int lineExec=Storage.exec_lines.get().values().stream().reduce(Integer::sum).get();
        Paragraph basicInfo = new Paragraph();
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph("Basic Coverage Info", bold));
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph(String.format("total_lines: %d\nexec_lines: %d\ncoverage_rate: %.3f%%\n",linesSum,lineExec, (double) 100*lineExec / (double) linesSum),smallBold));
        addEmptyLine(basicInfo, 3);
        document.add(basicInfo);

        //add table
        PdfPTable table = new PdfPTable(4);

        PdfPCell b = new PdfPCell(new Phrase("class_name"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("method_name"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("covered_lines/all_lines"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("coverage_rate(%)"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);
        table.setHeaderRows(1);

        for(String p:Storage.lines.get().keySet()){
            if(Storage.lines.get().get(p).size()>0){
                String[] s=p.split("#");
                table.addCell(s[0]);
                table.addCell(s[1]+s[2]);
                table.addCell(String.format("%d/%d",Storage.exec_lines.get().getOrDefault(p,0),Storage.lines.get().get(p).size()));
                table.addCell(String.valueOf((double) 100*Storage.exec_lines.get().getOrDefault(p,0)/(double)Storage.lines.get().get(p).size()));
            }
        }

        Paragraph content = new Paragraph();
        content.add(table);
        addEmptyLine(content,2);
        document.add(content);
        document.close();
    }

    private static void generateBranchCoverageReport(String path, Map<String,String> parameters) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        File f=new File(path);
        if(f.exists())f.delete();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        //add title
        if(!parameters.containsKey(titleKey)){
            parameters.put(titleKey,"Branch Coverage Report By Jacoconut");
        }
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(parameters.get(titleKey), bold));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(new Date().toString(),smallBold));
        addEmptyLine(preface, 1);
        document.add(preface);

        int branchesSum=Storage.branches.get().values().stream().mapToInt(List::size).sum();
        int branchExec=Storage.exec_branches.get().values().stream().reduce(Integer::sum).get();
        Paragraph basicInfo = new Paragraph();
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph("Basic Coverage Info", bold));
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph(String.format("total_branches: %d\nexec_branches: %d\ncoverage_rate: %.3f%%\n",branchesSum,branchExec, (double) 100*branchExec / (double) branchesSum),smallBold));
        addEmptyLine(basicInfo, 3);
        document.add(basicInfo);

        //add table
        PdfPTable table = new PdfPTable(3);

        PdfPCell b = new PdfPCell(new Phrase("method_name"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("covered_branches/all_branches"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("coverage_rate(%)"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);
        table.setHeaderRows(1);

        for(String p:Storage.branches.get().keySet()){
            if(Storage.branches.get().get(p).size()>0){
                table.addCell(p);
                table.addCell(String.format("%d/%d",Storage.exec_branches.get().getOrDefault(p,0),Storage.branches.get().get(p).size()));
                table.addCell(String.valueOf((double) 100*Storage.exec_branches.get().getOrDefault(p,0)/(double)Storage.branches.get().get(p).size()));
            }
        }

        Paragraph content = new Paragraph();
        content.add(table);
        addEmptyLine(content,2);
        document.add(content);
        document.close();
    }

    private static void generateMethodCoverageReport(String path, Map<String,String> parameters) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        File f=new File(path);
        if(f.exists())f.delete();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        //add title
        if(!parameters.containsKey(titleKey)){
            parameters.put(titleKey,"Method Coverage Report By Jacoconut");
        }
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(parameters.get(titleKey), bold));
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(new Date().toString(),smallBold));
        addEmptyLine(preface, 1);
        document.add(preface);

        int methodsSum=Storage.methods.get().values().stream().mapToInt(Set::size).reduce(Integer::sum).getAsInt();
        int methodExec=Storage.exec_methods.get().values().stream().reduce(Integer::sum).get();
        Paragraph basicInfo = new Paragraph();
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph("Basic Coverage Info", bold));
        addEmptyLine(basicInfo, 1);
        basicInfo.add(new Paragraph(String.format("total_methods: %d\nexec_methods: %d\ncoverage_rate: %.3f%%\n",methodsSum,methodExec, (double) 100*methodExec / (double) methodsSum),smallBold));
        addEmptyLine(basicInfo, 3);
        document.add(basicInfo);

        //add table
        PdfPTable table = new PdfPTable(3);

        PdfPCell b = new PdfPCell(new Phrase("class_name"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("covered_methods/all_methods"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);

        b = new PdfPCell(new Phrase("coverage_rate(%)"));
        b.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(b);
        table.setHeaderRows(1);

        for(String c:Storage.methods.get().keySet()){
            if(Storage.methods.get().get(c).size()>0){
                table.addCell(c);
                table.addCell(String.format("%d/%d",Storage.exec_methods.get().getOrDefault(c,0),Storage.methods.get().get(c).size()));
                table.addCell(String.valueOf((double) 100*Storage.exec_methods.get().getOrDefault(c,0)/(double)Storage.methods.get().get(c).size()));
            }
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
