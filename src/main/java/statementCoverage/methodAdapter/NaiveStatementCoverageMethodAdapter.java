package statementCoverage.methodAdapter;

import com.sun.xml.internal.ws.org.objectweb.asm.Label;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import storage.Storage;
import utils.Tracer;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;

/**
 * 行覆盖
 * 语句执行前插入探针
 */
public class NaiveStatementCoverageMethodAdapter extends MethodAdapter {
    Logger logger = Logger.getLogger(NaiveStatementCoverageMethodAdapter.class);
    String name;


    protected NaiveStatementCoverageMethodAdapter(MethodVisitor m, String n) {
        super(m);
        name = n;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);
        int currentLines=Storage.lines.get();
        Storage.lines.compareAndSet(currentLines,currentLines+1);
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "utils/Tracer", "getInstance", "()L"
                        + "utils/Tracer" + ";");
        mv.visitLdcInsn(1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "utils/Tracer", "executeLines2",
                "(I)V");
    }

}