package my;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import coverage.StatementCoverageMethodAdapter;

public class StatementCoverageClassAdaptor extends ClassAdapter {
    public StatementCoverageClassAdaptor(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor=this.cv.visitMethod(access,name,desc,signature,exceptions);
        return new StatementCoverageMethodAdapter(methodVisitor,name);
    }
}
