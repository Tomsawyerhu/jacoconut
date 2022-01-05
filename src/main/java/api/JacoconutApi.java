package api;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import statementCoverage.classAdapter.StatementCoverageClassAdaptor;
import statementCoverage.methodAdapter.SCType;
import storage.Storage;
import utils.Calculator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

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
        }else if(lcType==LCType.BASIC_BLOCK_RECORD){
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

    public static double calculateCoverage(){
        return Calculator.calculateStatementCoverage();
    }

    public static void reset(){
        Storage.lines.set(0);
        Storage.executeLines.set(0);
        Storage.probes.set(new ConcurrentHashMap<>());
    }

    /*
     * for test
     */
    public static int getLine(){
        return Storage.lines.get();
    }

    /*
     * for test
     */
    public static int getExec(){
        return Storage.executeLines.get();
    }
}
