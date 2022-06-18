package instru.offline;

import analyze.*;
import com.github.javaparser.utils.Pair;
import core.edge.EdgeSootDriver;
import externX.JacoconutX;
import instru.transformer.ClassTransformer;
import junit.TestDetector;
import junit.TestDriver;
import core.block.BlockSootDriver;
import org.apache.log4j.Logger;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import storage.Storage;
import storage.StorageHandler;
import utils.XmlReader;
import utils.XmlType;
import utils.XmlWriter;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JacoconutApi {
    private static Logger logger= Logger.getLogger(JacoconutApi.class);

    public static void lineCoverageProbe(String classFile) throws IOException {
        byte[] data = ClassTransformer.transform1(Files.readAllBytes(Paths.get(classFile)));
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static void lineCoverageProbes(String project) throws IOException{
        for(String classFile:findAllClassFiles(Paths.get(project,"target","classes"))){
            lineCoverageProbe(classFile);
        }
    }

    public static void branchCoverageProbe(String classFile) throws IOException {
        byte[] data = ClassTransformer.transform2(Files.readAllBytes(Paths.get(classFile)));
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static void branchCoverageProbes(String project) throws IOException {
        for(String classFile:findAllClassFiles(Paths.get(project,"target","classes"))){
            branchCoverageProbe(classFile);
        }
    }

    public static void methodCoverageProbe(String classFile) throws IOException {
        byte[] data = ClassTransformer.transform3(Files.readAllBytes(Paths.get(classFile)));
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static void methodCoverageProbes(String project) throws IOException {
        for(String classFile:findAllClassFiles(Paths.get(project,"target","classes"))){
            methodCoverageProbe(classFile);
        }
    }

    public static void testEndProbe(String classFile) throws IOException {
        byte[] data = ClassTransformer.transform4(Files.readAllBytes(Paths.get(classFile)));
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static void testEndProbes(String project) throws IOException {
        for(String classFile:Storage.tests.get().keySet()){
            testEndProbe(project+"/target/test-classes/"+classFile.replace(".","/")+".class");
        }
    }

    public static void preparation1(String project) throws VerificationException {
        //编译
        Verifier v=new Verifier(project);
        v.executeGoals(Collections.singletonList("test-compile"));
    }

    public static void preparation2(String project) throws IOException {
        //复制class文件
        File f=Paths.get(project,"target","classes","externX").toFile();
        if(f.exists()){f.delete();}
        f.mkdir();
        File jacoconutX=Paths.get(project,"target","classes","externX","JacoconutX.class").toFile();
        jacoconutX.createNewFile();
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            String p=new File("").getCanonicalPath();
            inputChannel = new FileInputStream(Paths.get(p,"target","classes","externX","JacoconutX.class").toAbsolutePath().toString()).getChannel();
            outputChannel = new FileOutputStream(jacoconutX).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }

    }

    private static List<String> findAllClassFiles(Path dir){
        try {
            return Files.walk(dir)
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean lineCoverage(String project) {
        try {
            String projectName=new File(project).getName();
            clean(project);
            StorageHandler.reset();
            logger.info(String.format("ready to compile project %s...",projectName));
            preparation1(project);
            logger.info(String.format("compile project %s done!",projectName));
            logger.info("find tests...");
            new TestDetector(project).detectAllJunitTests();
            logger.info("find tests done!");
            logger.info("modify source bytecode...");
            lineCoverageProbes(project);
            logger.info("modify source bytecode done!");
            logger.info("modify test bytecode...");
            testEndProbes(project);
            logger.info("modify test bytecode done!");
            logger.info("copy class file...");
            preparation2(project);
            logger.info("copy class file done!");
        } catch (IOException | VerificationException e) {
            logger.error(e.getMessage());
            return false;
        }

        //int a=Storage.lines.get().size();

        try {
            TestDriver t=new TestDriver(project);
            t.runAllTests();
            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","line_coverage"));
            StatementAnalyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\line_coverage_xml", XmlType.STATEMENT_COVERAGE);
            logger.info(String.format("generate %s xml report done!","line_coverage"));
        } catch (IOException | VerificationException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    public static Map<String,Set<Integer>> lineCoverageResult(String project){
        try {
            return XmlReader.readXml(project+File.separator+"line_coverage_xml",XmlType.STATEMENT_COVERAGE);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static Map<String, Pair<Integer,Integer>> lineCoverageReport(String project){
        try {
            return XmlReader.readXml2(project+File.separator+"line_coverage_xml",XmlType.STATEMENT_COVERAGE);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void branchCoverage(String project){
        try {
            String projectName=new File(project).getName();
            clean(project);
            StorageHandler.reset();
            logger.info(String.format("ready to compile project %s...",projectName));
            preparation1(project);
            logger.info(String.format("compile project %s done!",projectName));
            logger.info("find tests...");
            new TestDetector(project).detectAllJunitTests();
            logger.info("find tests done!");
            logger.info("modify source bytecode...");
            branchCoverageProbes(project);
            logger.info("modify source bytecode done!");
            logger.info("modify test bytecode...");
            testEndProbes(project);
            logger.info("modify test bytecode done!");
            logger.info("copy class file...");
            preparation2(project);
            logger.info("copy class file done!");
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }

        try {
            TestDriver t=new TestDriver(project);
            t.runAllTests();

            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","branch_coverage"));
            BranchAnalyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\branch_coverage_xml", XmlType.BRANCH_COVERAGE);
            logger.info(String.format("generate %s xml report done!","branch_coverage"));

        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

    public static void methodCoverage(String project){
        try {
            String projectName=new File(project).getName();
            clean(project);
            StorageHandler.reset();
            logger.info(String.format("ready to compile project %s...",projectName));
            preparation1(project);
            logger.info(String.format("compile project %s done!",projectName));
            logger.info("find tests...");
            new TestDetector(project).detectAllJunitTests();
            logger.info("find tests done!");
            logger.info("modify source bytecode...");
            methodCoverageProbes(project);
            logger.info("modify source bytecode done!");
            logger.info("modify test bytecode...");
            testEndProbes(project);
            logger.info("modify test bytecode done!");
            logger.info("copy class file...");
            preparation2(project);
            logger.info("copy class file done!");
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }

        try {
            TestDriver t=new TestDriver(project);
            t.runAllTests();

            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","method_coverage"));
            MethodAnalyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\method_coverage_xml", XmlType.METHOD_COVERAGE);
            logger.info(String.format("generate %s xml report done!","method_coverage"));

        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

    public static void blockCoverage(String project){
        try {
            String projectName=new File(project).getName();
            clean(project);
            StorageHandler.reset();
            logger.info(String.format("ready to compile project %s...",projectName));
            preparation1(project);
            logger.info(String.format("compile project %s done!",projectName));
            logger.info("find tests...");
            new TestDetector(project).detectAllJunitTests();
            logger.info("find tests done!");
            logger.info("modify source bytecode...");
            BlockSootDriver.main(new String[]{project});
            logger.info("modify source bytecode done!");
            logger.info("modify test bytecode...");
            testEndProbes(project);
            logger.info("modify test bytecode done!");
            logger.info("copy class file...");
            preparation2(project);
            logger.info("copy class file done!");
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }

        try {
            TestDriver t=new TestDriver(project);
            t.runAllTests();

            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","block_coverage"));
            BlockAnalyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\block_coverage_xml", XmlType.BLOCK_COVERAGE);
            logger.info(String.format("generate %s xml report done!","block_coverage"));

        } catch (VerificationException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void edgeCoverage(String project){
        try {
            String projectName=new File(project).getName();
            clean(project);
            StorageHandler.reset();
            logger.info(String.format("ready to compile project %s...",projectName));
            preparation1(project);
            logger.info(String.format("compile project %s done!",projectName));
            logger.info("find tests...");
            new TestDetector(project).detectAllJunitTests();
            logger.info("find tests done!");
            logger.info("modify source bytecode...");
            EdgeSootDriver.main(new String[]{project});
            logger.info("modify source bytecode done!");
            logger.info("modify test bytecode...");
            testEndProbes(project);
            logger.info("modify test bytecode done!");
            logger.info("copy class file...");
            preparation2(project);
            logger.info("copy class file done!");
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }

        try {
            TestDriver t=new TestDriver(project);
            t.runAllTests();

            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","edge_coverage"));
            EdgeAnalyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\edge_coverage_xml", XmlType.EDGE_COVERAGE);
            logger.info(String.format("generate %s xml report done!","edge_coverage"));
        } catch (VerificationException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void clean(String project){
        File file=new File(project+"/"+JacoconutX.output);
        if (file.exists()){file.delete();}
    }

    public static void main(String[] args) {
//        String[] projects=new String[]{
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-compress-rel-1.12",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-dbutils-DBUTILS_1_7",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-email-EMAIL_1_5",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-exec-1.3-RC1",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-fileupload-FILEUPLOAD_1_3",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-funccommons-exec-1.3-RC1tor-FUNCTOR_1_0_RC1",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-io-commons-io-2.6-RC3",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-pool-commons-pool-2.6.2",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-text-commons-text-1.0",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-validator-VALIDATOR_1_7",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\cucumber-reporting-cucumber-reporting-4.7.0",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\dictomaton-dictomaton-1.2.1",
//                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\elasticsearch-analysis-pinyin"
//        };
//        for(String p:projects){
//            lineCoverage(p);
//            branchCoverage(p);
//            methodCoverage(p);
//        }

        String p="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\elasticsearch-analysis-pinyin";
        blockCoverage(p);
        logger.info(XmlReader.calculateCoverage(p+"\\block_coverage_xml",XmlType.BLOCK_COVERAGE));
    }

}
