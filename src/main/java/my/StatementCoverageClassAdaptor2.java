package my;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;

public class StatementCoverageClassAdaptor2 extends ClassAdapter {
    public StatementCoverageClassAdaptor2(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor=this.cv.visitMethod(access,name,desc,signature,exceptions);
        return new StatementCoverageMethodAdapter.StatementCoverageMethodAdapterExecutor(methodVisitor,name);
    }
}
