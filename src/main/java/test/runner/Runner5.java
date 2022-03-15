package test.runner;

import analyze.BranchAnalyzer;
import api.JacoconutApi;
import com.itextpdf.text.DocumentException;
import junit.TestDetector;
import org.apache.log4j.Logger;
import visualize.Reporter;
import visualize.XmlWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class Runner5 {
    private static Logger logger= Logger.getLogger(Runner5.class);
    public static void main(String[] args) {
        String project="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4";
        BranchAnalyzer analyzer=new BranchAnalyzer();
        try {
            JacoconutApi.branchCoverageProbes(project);
            analyzer.analyze(Paths.get(project,"probe_info.jcn").toFile());
            analyzer.analyze2(Paths.get(project,"probe_info.jcn").toFile());
            new TestDetector(project).detectAllJunitTests();
            Reporter.generateReport(project+"\\branch_coverage_pdf", Reporter.ReportType.BRANCH_COVERAGE,new HashMap<>());
            XmlWriter.generateXml("D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\branch_coverage", XmlWriter.XmlType.BRANCH_COVERAGE);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

    }
}
