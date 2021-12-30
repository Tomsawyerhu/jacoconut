package api;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import statementCoverage.classAdapter.StatementCoverageClassAdaptor;
import statementCoverage.methodAdapter.SCType;

import java.io.FileOutputStream;
import java.io.IOException;

public class JacoconutApi {
    public static void lineCoverageProbe(String className,String classFile) throws IOException {
        lineCoverageProbe(className,classFile,LCType.NAIVE);
    }

    public static void lineCoverageProbe(String className,String classFile,LCType lcType) throws IOException {
        ClassReader cr=new ClassReader(className);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StatementCoverageClassAdaptor classVisitor = null;
        if(lcType==LCType.NAIVE){
            classVisitor = new StatementCoverageClassAdaptor(cw, SCType.NAIVE);
        }else if(lcType==LCType.BASIC_BLOCK_RECORD){
            classVisitor = new StatementCoverageClassAdaptor(cw, SCType.BASIC_BLOCK_RECORD);
        }else{
            return;
        }
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        if(lcType==LCType.NAIVE){
            //write
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream(classFile);
            fos.write(data);
            fos.flush();
            fos.close();
            return;
        }

        cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StatementCoverageClassAdaptor classVisitor2 = new StatementCoverageClassAdaptor(cw,SCType.BASIC_BLOCK_EXEC);
        cr.accept(classVisitor2, ClassReader.SKIP_FRAMES);

        //write
        byte[] data = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(classFile);
        fos.write(data);
        fos.flush();
        fos.close();
    }
}
