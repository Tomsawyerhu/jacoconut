package instru.transformer;

import core.CoverageClassAdapter;
import core.SCType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ClassTransformer {
    //行覆盖
    public static byte[] transform1(byte[] classfile){
        ClassReader cr=new ClassReader(classfile);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter classVisitor;
        classVisitor = new CoverageClassAdapter(cw, SCType.STATEMENT_NAIVE);
        cr.accept(classVisitor, ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    //分支覆盖
    public static byte[] transform2(byte[] classfile){
        ClassReader cr=new ClassReader(classfile);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter branchCoverageClassAdapter=new CoverageClassAdapter(cw,SCType.BRANCH);
        cr.accept(branchCoverageClassAdapter,ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    //方法覆盖
    public static byte[] transform3(byte[] classfile){
        ClassReader cr=new ClassReader(classfile);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter methodCoverageClassAdapter=new CoverageClassAdapter(cw,SCType.METHOD);
        cr.accept(methodCoverageClassAdapter,ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }

    //测试用例
    public static byte[] transform4(byte[] classfile) {
        ClassReader cr = new ClassReader(classfile);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        CoverageClassAdapter testEndClassAdapter = new CoverageClassAdapter(cw, SCType.TEST_END);
        cr.accept(testEndClassAdapter, ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }
}
