package coverage.classAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import coverage.methodAdapter.CoverageMethodAdapterFactory;


public class CoverageClassAdapter extends ClassVisitor {
    private String name;

    public CoverageClassAdapter(ClassVisitor cv) {
        super(458752,cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name=name;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        return CoverageMethodAdapterFactory.getMethodVisitor(this.name,name+"#"+desc, methodVisitor);
    }
}
