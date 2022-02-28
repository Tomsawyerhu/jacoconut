package api;

import externX.JacoconutX;
import junit.TestDetector;
import junit.TestDriver;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import statementCoverage.classAdapter.StatementCoverageClassAdaptor;
import statementCoverage.methodAdapter.SCType;
import storage.Storage;
import storage.StorageHandler;
import utils.Calculator;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JacoconutApi {
    public static void lineCoverageProbe(String classFile) throws IOException {
        lineCoverageProbe(classFile,LCType.NAIVE);
    }

    public static void lineCoverageProbe(String classFile,LCType lcType) throws IOException {
        FileInputStream inputStream=new FileInputStream(classFile);
        ClassReader cr=new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StatementCoverageClassAdaptor classVisitor;
        if(lcType==LCType.NAIVE){
            classVisitor = new StatementCoverageClassAdaptor(cw, SCType.NAIVE);
        }else if(lcType==LCType.BASIC_BLOCK){
            classVisitor = new StatementCoverageClassAdaptor(cw, SCType.BASIC_BLOCK_RECORD);
        }else{
            inputStream.close();
            return;
        }
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        inputStream.close();
        if(lcType==LCType.NAIVE){
            //write
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(data);
            fos.flush();
            fos.close();
            return;
        }

        inputStream=new FileInputStream(classFile);
        cr=new ClassReader(inputStream);

        cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StatementCoverageClassAdaptor classVisitor2 = new StatementCoverageClassAdaptor(cw,SCType.BASIC_BLOCK_EXEC);
        cr.accept(classVisitor2, ClassReader.SKIP_FRAMES);

        inputStream.close();
        //write
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

    public static void lineCoverageProbes(String project,LCType lcType) throws IOException{
        for(String classFile:findAllClassFiles(Paths.get(project,"target","classes"))){
            System.out.println("start "+classFile);
            lineCoverageProbe(classFile,lcType);
            System.out.println("finish "+classFile);
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

    public static double calculateCoverage(){
        return Calculator.calculateStatementCoverage();
    }

    /*
     * for test
     */
    public static int getLine(){
        return Storage.lines.get();
    }

    public static void main(String[] args) throws  VerificationException {
        String p="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-codec-1_5_RELEASE";
        preparation1(p);
        System.out.println("end preparation1");
        try {
            lineCoverageProbes(p,LCType.BASIC_BLOCK);
            preparation2(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map<String,List<String>> m=new TestDetector(p).detectAllJunitTests();
            TestDriver t=new TestDriver(p);

            for (String clazz:m.keySet()){
                for(String method:m.get(clazz)){
                    t.run(clazz,method);
                    String path=JacoconutX.output;
                    FileWriter writer=new FileWriter(Paths.get(p,path).toFile(),true);
                    writer.write(String.format("--------------------\ntest_method:%s#%s\ntest_type:%s\n--------------------\n",clazz,method,"line_coverage"));
                    writer.flush();
                    writer.close();
                }
                StorageHandler.resetProbe();
            }
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

}
