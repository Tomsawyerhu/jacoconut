package test.runner;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import coverage.ECGCoverageListener;
import my.StatementCoverageClassAdaptor;
import test.TestB;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestRunnerB {
    public static void main(String[] args) {
        ClassReader cr = null;
        try {
            cr = new ClassReader("test/TestB");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            StatementCoverageClassAdaptor classVisitor = new StatementCoverageClassAdaptor(cw);
            cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
            byte[] data = cw.toByteArray();
            FileOutputStream fos = new FileOutputStream("C:\\Users\\tom\\Desktop\\jacoconut\\target\\classes\\test\\TestB.class");
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new TestB().testCoverage();
        System.out.println(ECGCoverageListener.lines.get());
        System.out.println(ECGCoverageListener.probes.get());
    }
}
