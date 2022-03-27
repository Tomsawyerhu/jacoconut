package api;

import algorithm.cfg;
import analyze.BranchAnalyzer;
import analyze.MethodAnalyzer;
import analyze.PathAnalyzer;
import analyze.StatementAnalyzer;
import com.itextpdf.text.DocumentException;
import coverage.methodAdapter.CfgMethodAdapter;
import externX.JacoconutX;
import junit.TestDetector;
import junit.TestDriver;
import org.apache.log4j.Logger;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import coverage.classAdapter.CoverageClassAdapter;
import coverage.methodAdapter.SCType;
import storage.Storage;
import storage.StorageHandler;
import visualize.Reporter;
import visualize.XmlWriter;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JacoconutApi {
    private static Logger logger= Logger.getLogger(JacoconutApi.class);

    public static void lineCoverageProbe(String classFile) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter classVisitor;
        classVisitor = new CoverageClassAdapter(cw, SCType.STATEMENT_NAIVE);
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        inputStream.close();
        byte[] data = cw.toByteArray();
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

    public static void labelCoverageProbe(String classFile) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter labelCoverageClassAdapter=new CoverageClassAdapter(cw,SCType.PATH);
        cr.accept(labelCoverageClassAdapter,ClassReader.SKIP_FRAMES);
        inputStream.close();
        byte[] data = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public static void branchCoverageProbe(String classFile) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter branchCoverageClassAdapter=new CoverageClassAdapter(cw,SCType.BRANCH);
        cr.accept(branchCoverageClassAdapter,ClassReader.SKIP_FRAMES);
        inputStream.close();
        byte[] data = cw.toByteArray();
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


    public static void pathCoverageProbe(String classFile) throws IOException {
        FileInputStream inputStream= null;
        try {
            inputStream = new FileInputStream(classFile);
            ClassReader cr=new ClassReader(inputStream);
            CoverageClassAdapter coverageClassAdapter=new CoverageClassAdapter(null,SCType.CFG);
            cr.accept(coverageClassAdapter,ClassReader.SKIP_FRAMES);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream = new FileInputStream(classFile);
            ClassReader cr=new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            CoverageClassAdapter coverageClassAdapter=new CoverageClassAdapter(cw,SCType.METHOD_START_END);
            cr.accept(coverageClassAdapter,ClassReader.SKIP_FRAMES);
            inputStream.close();
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        labelCoverageProbe(classFile);
    }

    public static void pathCoverageProbes(String project) throws IOException {
        for(String classFile:findAllClassFiles(Paths.get(project,"target","classes"))){
            pathCoverageProbe(classFile);
        }
    }

    public static void testEndProbe(String classFile) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter testEndClassAdapter=new CoverageClassAdapter(cw,SCType.TEST_END);
        cr.accept(testEndClassAdapter,ClassReader.SKIP_FRAMES);
        inputStream.close();
        byte[] data = cw.toByteArray();
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

    public static void methodCoverageProbe(String classFile) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter methodCoverageClassAdapter=new CoverageClassAdapter(cw,SCType.METHOD);
        cr.accept(methodCoverageClassAdapter,ClassReader.SKIP_FRAMES);
        inputStream.close();
        byte[] data = cw.toByteArray();
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



    public static void lineCoverage(String project) {
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
            e.printStackTrace();
        }

        //int a=Storage.lines.get().size();

        try {
            TestDriver t=new TestDriver(project);
            t.runAllTests();

            //生成pdf报告
            logger.info(String.format("ready to generate %s pdf report...","line_coverage"));
            StatementAnalyzer analyzer=new StatementAnalyzer();
            analyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            Reporter.generateReport(project +"\\line_coverage_pdf", Reporter.ReportType.STATEMENT_COVERAGE,new HashMap<>());
            logger.info(String.format("generate %s pdf report done!","line_coverage"));
            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","line_coverage"));
            analyzer.reset();
            analyzer.analyze2(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\line_coverage_xml", XmlWriter.XmlType.STATEMENT_COVERAGE);
            logger.info(String.format("generate %s xml report done!","line_coverage"));
        } catch (IOException | VerificationException | DocumentException e) {
            e.printStackTrace();
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

            //生成pdf报告
            logger.info(String.format("ready to generate %s pdf report...","branch_coverage"));
            BranchAnalyzer analyzer=new BranchAnalyzer();
            analyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            Reporter.generateReport(project +"\\branch_coverage_pdf", Reporter.ReportType.BRANCH_COVERAGE,new HashMap<>());
            logger.info(String.format("generate %s pdf report done!","branch_coverage"));

            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","branch_coverage"));
            analyzer.reset();
            analyzer.analyze2(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\branch_coverage_xml", XmlWriter.XmlType.BRANCH_COVERAGE);
            logger.info(String.format("generate %s xml report done!","branch_coverage"));

        } catch (IOException | VerificationException | DocumentException e) {
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

            //生成pdf报告
            logger.info(String.format("ready to generate %s pdf report...","method_coverage"));
            MethodAnalyzer analyzer=new MethodAnalyzer();
            analyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            Reporter.generateReport(project +"\\method_coverage_pdf", Reporter.ReportType.METHOD_COVERAGE,new HashMap<>());
            logger.info(String.format("generate %s pdf report done!","method_coverage"));

            //生成xml报告
            logger.info(String.format("ready to generate %s xml report...","method_coverage"));
            analyzer.reset();
            analyzer.analyze2(Paths.get(project,JacoconutX.output).toFile());
            XmlWriter.generateXml(project+"\\method_coverage_xml", XmlWriter.XmlType.METHOD_COVERAGE);
            logger.info(String.format("generate %s xml report done!","method_coverage"));

        } catch (IOException | VerificationException | DocumentException e) {
            e.printStackTrace();
        }
    }


    public static void pathCoverage(String project)  {

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
            pathCoverageProbes(project);
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
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }

        //计算路径数量&绘制cfg
        cfg.CfgPathOptions options=new cfg.CfgPathOptions();
        options.limit_loop_times=1;
        int i=1;
        for(CfgMethodAdapter.ControlFlowGraph c: Storage.cfgs.get()){
            try {
                cfg.cfgDrawer(c,Paths.get(project,"pic"+i+".png").toAbsolutePath().toString());
                logger.info(c.className+"#"+c.methodName+":"+i);
                String key=c.className+"#"+c.methodName;
                int paths=cfg.cfgPaths(c,options);
                StorageHandler.setPath(key,cfg.cfgPaths(c,options));
                logger.info(c.className+"#"+c.methodName+":"+paths);
            } catch (IOException e) {
                e.printStackTrace();
            }

            i+=1;
        }

        try {
            //生成pdf报告
            logger.info(String.format("ready to generate %s pdf report...","path_coverage"));
            PathAnalyzer analyzer=new PathAnalyzer();
            analyzer.analyze(Paths.get(project,JacoconutX.output).toFile());
            Reporter.generateReport(project +"\\path_coverage_pdf", Reporter.ReportType.PATH_COVERAGE,new HashMap<>());
            logger.info(String.format("generate %s pdf report done!","path_coverage"));
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

    }
    
    public static void clean(String project){
        File file=new File(project+"/"+JacoconutX.output);
        if (file.exists()){file.delete();}
    }

    public static void main(String[] args) {
        String[] projects=new String[]{
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-compress-rel-1.12",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-dbutils-DBUTILS_1_7",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-email-EMAIL_1_5",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-exec-1.3-RC1",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-fileupload-FILEUPLOAD_1_3",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-functor-FUNCTOR_1_0_RC1",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-io-commons-io-2.6-RC3",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-pool-commons-pool-2.6.2",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-text-commons-text-1.0",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-validator-VALIDATOR_1_7",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\cucumber-reporting-cucumber-reporting-4.7.0",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\dictomaton-dictomaton-1.2.1",
                "D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\elasticsearch-analysis-pinyin"
        };
        for(String p:projects){
            lineCoverage(p);
            branchCoverage(p);
            methodCoverage(p);
        }

//        String p="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\elasticsearch-analysis-pinyin";
//        methodCoverage(p);
    }

}
