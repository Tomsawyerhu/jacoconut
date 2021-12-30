package statementCoverage.classAdapter;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import statementCoverage.methodAdapter.SCType;
import statementCoverage.methodAdapter.StatementCoverageByBasicBlockMethodAdapter;
import statementCoverage.methodAdapter.StatementCoverageMethodAdapterFactory;

public class StatementCoverageClassAdaptor extends ClassAdapter {
    private final SCType methodVisitorType;
    public StatementCoverageClassAdaptor(ClassVisitor cv, SCType scType) {
        super(cv);
        this.methodVisitorType=scType;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor=this.cv.visitMethod(access,name,desc,signature,exceptions);
        return StatementCoverageMethodAdapterFactory.getMethodVisitor(methodVisitorType,name,methodVisitor);
    }
}
