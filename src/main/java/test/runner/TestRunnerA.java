package test.runner;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import my.Calculator;
import my.StatementCoverageClassAdaptor;
import my.StatementCoverageClassAdaptor2;
import test.TestA;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestRunnerA {
    public static void main(String[] args) {
        ClassReader cr = null;
        try {
            cr = new ClassReader("test/TestA");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            StatementCoverageClassAdaptor classVisitor = new StatementCoverageClassAdaptor(cw);
            cr.accept(classVisitor, ClassReader.SKIP_FRAMES);

            ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            StatementCoverageClassAdaptor2 classVisitor2 = new StatementCoverageClassAdaptor2(cw2);
            cr.accept(classVisitor2, ClassReader.SKIP_FRAMES);
            byte[] data = cw2.toByteArray();
            FileOutputStream fos = new FileOutputStream("C:\\Users\\tom\\Desktop\\jacoconut\\target\\classes\\test\\TestA.class");
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new TestA().func1(1);
        new Calculator().calculateStatementCoverage();
    }
}
