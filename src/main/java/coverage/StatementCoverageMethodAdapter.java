package coverage;

import com.sun.xml.internal.ws.org.objectweb.asm.Label;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;


public class StatementCoverageMethodAdapter extends MethodAdapter {
    Logger logger = Logger.getLogger(StatementCoverageMethodAdapter.class);
    String name;


    public StatementCoverageMethodAdapter(MethodVisitor m,String n) {
        super(m);
        name = n;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);
        //fixme
        Tracer.recordMethodInfo("",name,line);
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "coverage/Tracer", "getInstance", "()L"
                        + "coverage/Tracer" + ";");
        mv.visitLdcInsn("");
        mv.visitLdcInsn(name+":"+line);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "coverage/Tracer", "logMethodInfo",
                "(Ljava/lang/String;Ljava/lang/String;)V");
    }

}