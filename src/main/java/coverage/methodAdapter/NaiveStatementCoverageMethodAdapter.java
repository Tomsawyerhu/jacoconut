package coverage.methodAdapter;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import storage.Storage;
import storage.StorageHandler;

/**
 * 行覆盖
 * 语句执行前插入探针
 */
public class NaiveStatementCoverageMethodAdapter extends MethodVisitor {
    private static Logger logger = Logger.getLogger(NaiveStatementCoverageMethodAdapter.class);
    String className;
    String name;
    int lines=0;


    protected NaiveStatementCoverageMethodAdapter(MethodVisitor m, String n1,String n2) {
        super(458752,m);
        className=n1;
        name = n2;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);
        lines+=1;
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "externX/JacoconutX", "getInstance", "()L"
                        + "externX/JacoconutX" + ";");
        String callsite=className+"#"+name+"#"+line;
        mv.visitLdcInsn(callsite);
        mv.visitLdcInsn(1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "externX/JacoconutX", "executeLines",
                "(Ljava/lang/String;I)V");
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        StorageHandler.setLine(className+"#"+name,lines);
    }
}