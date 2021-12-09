package test;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import my.Calculator;
import my.StatementCoverageClassAdaptor;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestRunner {
    public static void main(String[] args) {
        ClassReader cr = null;
        try {
            cr = new ClassReader("test/TestA");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            StatementCoverageClassAdaptor classVisitor = new StatementCoverageClassAdaptor(cw);
            cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream("C:\\Users\\tom\\Desktop\\jacoconut\\target\\classes\\test\\TestA.class");
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new TestA().func1(true);
        new Calculator().calculateStatementCoverage();
    }
}
