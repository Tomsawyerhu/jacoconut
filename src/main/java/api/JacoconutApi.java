package api;

import algorithm.cfg;
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
       branchCoverageProbe(classFile);
        try {
            inputStream = new FileInputStream(classFile);
            ClassReader cr=new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            CoverageClassAdapter coverageClassAdapter=new CoverageClassAdapter(cw,SCType.METHOD_STSRT_END);
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
    }

    public static void pathCoverageProbes(String project) throws IOException {
        for(String classFile:findAllClassFiles(Paths.get(project,"target","classes"))){
            pathCoverageProbe(classFile);
        }

        cfg.CfgPathOptions options=new cfg.CfgPathOptions();
        options.limit_path_length=100;
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

    public static void lineCoverage(String project) throws  VerificationException {
        String p=project;
        logger.info("ready to compile...");
        preparation1(p);
        logger.info("compile done!");
        try {
            logger.info("modify bytecode...");
            lineCoverageProbes(p);
            logger.info("modify bytecode done!");
            logger.info("copy class file...");
            preparation2(p);
            logger.info("copy class file done!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map<String,List<String>> m=new TestDetector(p).detectAllJunitTests();
            TestDriver t=new TestDriver(p);

            for (String clazz:m.keySet()){
                for(String method:m.get(clazz)){
                    logger.info(String.format("start test:%s#%s",clazz,method));
                    t.run(clazz,method);
                    String path=JacoconutX.output;
                    FileWriter writer=new FileWriter(Paths.get(p,path).toFile(),true);
                    writer.write(String.format("--------------------\ntest_method:%s#%s\ntest_type:%s\n--------------------\n",clazz,method,"line_coverage"));
                    writer.flush();
                    writer.close();
                    logger.info(String.format("finish test:%s#%s",clazz,method));
                }
            }
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

    public static void branchCoverage(String project) throws  VerificationException {
        String p=project;
        logger.info("ready to compile...");
        preparation1(p);
        logger.info("compile done!");
        try {
            logger.info("modify bytecode...");
            branchCoverageProbes(p);
            logger.info("modify bytecode done!");
            logger.info("copy class file...");
            preparation2(p);
            logger.info("copy class file done!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map<String,List<String>> m=new TestDetector(p).detectAllJunitTests();
            TestDriver t=new TestDriver(p);

            for (String clazz:m.keySet()){
                for(String method:m.get(clazz)){
                    logger.info(String.format("start test:%s#%s",clazz,method));
                    t.run(clazz,method);
                    String path=JacoconutX.output;
                    FileWriter writer=new FileWriter(Paths.get(p,path).toFile(),true);
                    writer.write(String.format("--------------------\ntest_method:%s#%s\ntest_type:%s\n--------------------\n",clazz,method,"branch_coverage"));
                    writer.flush();
                    writer.close();
                    logger.info(String.format("finish test:%s#%s",clazz,method));
                }
            }
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

    public static void pathCoverage(String project) throws  VerificationException {
        String p=project;
        logger.info("ready to compile...");
        preparation1(p);
        logger.info("compile done!");
        try {
            logger.info("modify bytecode...");
            pathCoverageProbes(p);
            logger.info("modify bytecode done!");
            logger.info("copy class file...");
            preparation2(p);
            logger.info("copy class file done!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map<String,List<String>> m=new TestDetector(p).detectAllJunitTests();
            TestDriver t=new TestDriver(p);

            for (String clazz:m.keySet()){
                for(String method:m.get(clazz)){
                    logger.info(String.format("start test:%s#%s",clazz,method));
                    t.run(clazz,method);
                    String path=JacoconutX.output;
                    FileWriter writer=new FileWriter(Paths.get(p,path).toFile(),true);
                    writer.write(String.format("--------------------\ntest_method:%s#%s\ntest_type:%s\n--------------------\n",clazz,method,"path_coverage"));
                    writer.flush();
                    writer.close();
                    logger.info(String.format("finish test:%s#%s",clazz,method));
                }
            }
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String p="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4";
        try {
            pathCoverage(p);
        } catch (VerificationException e) {
            e.printStackTrace();
        }
    }

}
