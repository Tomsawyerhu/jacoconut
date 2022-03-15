package test.runner;

import analyze.StatementAnalyzer;
import api.JacoconutApi;
import com.itextpdf.text.DocumentException;
import org.apache.log4j.Logger;
import visualize.Reporter;
import visualize.XmlWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Runner4 {
    private static Logger logger= Logger.getLogger(Runner4.class);

    public static void main(String[] args) {
        String project="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4";
        try {
            StatementAnalyzer analyzer=new StatementAnalyzer();
            JacoconutApi.lineCoverageProbes(project);
            analyzer.analyze(new File("D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\probe_info.jcn"));
            Reporter.generateReport(project+"\\line_coverage_pdf", Reporter.ReportType.STATEMENT_COVERAGE,new HashMap<>());
            //XmlWriter.generateXml("D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\line_coverage", XmlWriter.XmlType.STATEMENT_COVERAGE);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}
