package test.runner;

import analyze.StatementAnalyzer;
import api.JacoconutApi;
import org.apache.log4j.Logger;
import visualize.XmlWriter;

import java.io.File;
import java.io.IOException;

public class Runner4 {
    private static Logger logger= Logger.getLogger(Runner4.class);
    public static void main(String[] args) {
        String project="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4";
        try {
            StatementAnalyzer analyzer=new StatementAnalyzer();
            JacoconutApi.lineCoverageProbes(project);
            analyzer.analyze2(new File("D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\probe_info.jcn"));
            XmlWriter.generateXml("D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\line_coverage", XmlWriter.XmlType.STATEMENT_COVERAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
