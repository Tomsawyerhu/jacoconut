package test.runner;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import statementCoverage.methodAdapter.SCType;
import statementCoverage.classAdapter.StatementCoverageClassAdaptor;
import test.TestA;
import utils.Calculator;

import java.io.FileOutputStream;
import java.io.IOException;


public class TestRunnerA {
    public static void main(String[] args) {
        ClassReader cr;
        try {
            cr = new ClassReader("test/TestA");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            StatementCoverageClassAdaptor classVisitor = new StatementCoverageClassAdaptor(cw, SCType.BASIC_BLOCK_RECORD);
            cr.accept(classVisitor, ClassReader.SKIP_FRAMES);

            cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            StatementCoverageClassAdaptor classVisitor2 = new StatementCoverageClassAdaptor(cw,SCType.BASIC_BLOCK_EXEC);
            cr.accept(classVisitor2, ClassReader.SKIP_FRAMES);
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream("C:\\Users\\tom\\Desktop\\jacoconut\\target\\classes\\test\\TestA.class");
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new TestA().func1(1);
        Calculator.calculateStatementCoverage();
    }
}
