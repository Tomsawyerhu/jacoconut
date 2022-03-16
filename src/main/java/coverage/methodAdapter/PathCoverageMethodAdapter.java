package coverage.methodAdapter;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class PathCoverageMethodAdapter extends MethodVisitor{
    private static Logger logger = Logger.getLogger(NaiveStatementCoverageMethodAdapter.class);
    String className;
    String name;
    private static int labelId=0;


    public PathCoverageMethodAdapter(MethodVisitor m, String n1, String n2) {
        super(458752,m);
        className=n1;
        name = n2;
    }

    @Override
    public void visitLabel(Label label) {
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "externX/JacoconutX", "getInstance", "()L"
                        + "externX/JacoconutX" + ";");
        mv.visitLdcInsn(className+"#"+name);
        mv.visitLdcInsn(labelId);
        labelId+=1;
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "externX/JacoconutX", "executeLabel",
                "(Ljava/lang/String;I)V");
        super.visitLabel(label);
    }
}
