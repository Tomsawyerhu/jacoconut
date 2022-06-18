package core.test;

import org.apache.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TestEndMethodAdapter extends MethodVisitor {
    private static Logger logger = Logger.getLogger(TestEndMethodAdapter.class);
    String className;
    String name;
    private boolean isTest=false;

    public TestEndMethodAdapter(MethodVisitor m, String n1, String n2) {
        super(458752,m);
        className=n1;
        name = n2;
//        isTest= Storage.tests.get().containsKey(n1.replace("/","."))&&Storage.tests.get().get(n1.replace("/",".")).contains(n2.split("#")[0]);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if(descriptor.startsWith("Lorg/junit/Test")){
            isTest=true;
        }
        return super.visitAnnotation(descriptor,visible);
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