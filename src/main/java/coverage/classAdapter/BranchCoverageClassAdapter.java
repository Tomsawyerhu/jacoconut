package coverage.classAdapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import coverage.methodAdapter.BranchCoverageMethodAdapter;

public class BranchCoverageClassAdapter extends ClassVisitor {
    private String name;

    public BranchCoverageClassAdapter(ClassVisitor classVisitor) {
        super(458752,classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name=name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv=super.visitMethod(access, name, descriptor, signature, exceptions);
        return new BranchCoverageMethodAdapter(mv,this.name,name+"#"+descriptor);
    }
}
