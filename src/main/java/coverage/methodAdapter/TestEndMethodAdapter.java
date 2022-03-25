package coverage.methodAdapter;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import storage.Storage;

public class TestEndMethodAdapter extends MethodVisitor {
    private static Logger logger = Logger.getLogger(TestEndMethodAdapter.class);
    String className;
    String name;
    private final boolean isTest;

    protected TestEndMethodAdapter(MethodVisitor m, String n1, String n2) {
        super(458752,m);
        className=n1;
        name = n2;
        isTest= Storage.tests.get().containsKey(n1.replace("/","."))&&Storage.tests.get().get(n1.replace("/",".")).contains(n2.split("#")[0]);
    }

    @Override
    public void visitInsn(int opcode) {
        if(isTest&&(opcode>= Opcodes.IRETURN && opcode<=Opcodes.RETURN)||opcode==Opcodes.ATHROW){
            //return、throw指令
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "externX/JacoconutX", "getInstance", "()L"
                            + "externX/JacoconutX" + ";");
            mv.visitLdcInsn(className+"#"+name);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "externX/JacoconutX", "testEnd",
                    "(Ljava/lang/String;)V");
        }
        super.visitInsn(opcode);
    }
}