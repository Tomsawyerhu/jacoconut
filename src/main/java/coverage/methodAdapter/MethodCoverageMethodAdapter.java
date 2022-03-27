package coverage.methodAdapter;

import org.apache.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import storage.Storage;

import java.util.HashSet;

public class MethodCoverageMethodAdapter extends MethodVisitor {
    private static Logger logger = Logger.getLogger(MethodCoverageMethodAdapter.class);
    String className;
    String name;

    protected MethodCoverageMethodAdapter(MethodVisitor m, String n1, String n2) {
        super(458752,m);
        className=n1;
        name = n2;
    }

    @Override
    public void visitInsn(int opcode) {
        if((opcode>= Opcodes.IRETURN && opcode<=Opcodes.RETURN)||opcode==Opcodes.ATHROW){
            //return、throw指令
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "externX/JacoconutX", "getInstance", "()L"
                            + "externX/JacoconutX" + ";");
            mv.visitLdcInsn(className+"#"+name);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "externX/JacoconutX", "executeMethod",
                    "(Ljava/lang/String;)V");
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitEnd() {
        Storage.methods.get().putIfAbsent(className,new HashSet<>());
        Storage.methods.get().get(className).add(name);
        super.visitEnd();
    }
}
