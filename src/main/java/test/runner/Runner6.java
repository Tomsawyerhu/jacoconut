package test.runner;

import analyze.BranchAnalyzer;
import analyze.PathAnalyzer;
import api.JacoconutApi;
import com.itextpdf.text.DocumentException;
import junit.TestDetector;
import visualize.Reporter;
import visualize.XmlWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class Runner6 {
    public static void main(String[] args) {
        String project="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4";
        PathAnalyzer analyzer=new PathAnalyzer();
        try {
            JacoconutApi.pathCoverageProbes(project);
            analyzer.analyze(Paths.get(project,"probe_info.jcn").toFile());
            new TestDetector(project).detectAllJunitTests();
            Reporter.generateReport(project+"\\path_coverage_pdf", Reporter.ReportType.PATH_COVERAGE,new HashMap<>());
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}
