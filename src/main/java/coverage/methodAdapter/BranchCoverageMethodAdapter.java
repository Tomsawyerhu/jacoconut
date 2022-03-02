package coverage.methodAdapter;

import com.github.javaparser.utils.Pair;
import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.util.*;

public class BranchCoverageMethodAdapter extends MethodVisitor {
    private static Logger logger = Logger.getLogger(BranchCoverageMethodAdapter.class);
    private final String className;
    private final String methodName;
    private int line=-1;

    private Map<Label,Pair<Integer,Integer>> switchLabels = new HashMap<>();
    private static int branchId=0;
    public static List<BranchStruct> branchList=new ArrayList<>();

    public BranchCoverageMethodAdapter(MethodVisitor mv,String n1,String n2) {
        super(458752,mv);
        className=n1;
        methodName=n2;
    }

    @Override
    public void visitLabel(Label label) {
        String callsite=className+"#"+methodName;
        super.visitLabel(label);
        if (switchLabels.containsKey(label)) {
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "externX/JacoconutX", "getInstance", "()L"
                            + "externX/JacoconutX" + ";");
            mv.visitLdcInsn(callsite);
            mv.visitLdcInsn(switchLabels.get(label).a);
            mv.visitLdcInsn(switchLabels.get(label).b);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "externX/JacoconutX", "executeBranch",
                    "(Ljava/lang/String;II)V");
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String callsite=this.className+"#"+this.methodName;
        int which;

        if (opcode == org.objectweb.asm.Opcodes.GOTO) {
            //收集分支
            branchId+=1;
            branchList.add(new BranchStruct(branchId,callsite,new int[]{line}));

            //修改字节码
            which=0;
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "externX/JacoconutX", "getInstance", "()L"
                            + "externX/JacoconutX" + ";");
            mv.visitLdcInsn(callsite);
            mv.visitLdcInsn(branchId);
            mv.visitLdcInsn(which);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "externX/JacoconutX", "executeBranch",
                    "(Ljava/lang/String;II)V");

            mv.visitJumpInsn(opcode, label);
            return;
        }

        //收集分支
        branchId+=1;
        Field line= null;
        try {
            line = label.getClass().getDeclaredField("lineNumber");
            line.setAccessible(true);
            branchList.add(new BranchStruct(branchId,callsite,new int[]{this.line,line.getInt(label)}));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        //修改字节码
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "externX/JacoconutX", "getInstance", "()L"
                        + "externX/JacoconutX" + ";");
        mv.visitLdcInsn(callsite);
        mv.visitLdcInsn(branchId);
        mv.visitLdcInsn(false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "externX/JacoconutX", "executeBranch",
                "(Ljava/lang/String;IZ)V");

        mv.visitJumpInsn(opcode, label);

        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "externX/JacoconutX", "getInstance", "()L"
                        + "externX/JacoconutX" + ";");
        mv.visitLdcInsn(callsite);
        mv.visitLdcInsn(branchId);
        mv.visitLdcInsn(true);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "externX/JacoconutX", "executeBranch",
                "(Ljava/lang/String;IZ)V");
    }


    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        String callsite=this.className+"#"+this.methodName;
        branchId+=1;
        int which=0;
        int[] wheres=new int[labels.length];
        for(Label label:labels){
            try {
                Field line = label.getClass().getDeclaredField("lineNumber");
                line.setAccessible(true);
                wheres[which]=line.getInt(label);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            this.switchLabels.put(label, new Pair<>(branchId, which));
            which+=1;
        }
        switchLabels.remove(dflt);
        branchList.add(new BranchStruct(branchId,callsite,wheres));

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        String callsite=this.className+"#"+this.methodName;
        branchId+=1;
        int which=0;
        int[] wheres=new int[labels.length];
        for(Label label:labels){
            try {
                Field line = label.getClass().getDeclaredField("lineNumber");
                line.setAccessible(true);
                wheres[which]=line.getInt(label);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            this.switchLabels.put(label, new Pair<>(branchId, which));
            which+=1;
        }
        switchLabels.remove(dflt);
        branchList.add(new BranchStruct(branchId,callsite,wheres));
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.line=line;
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack+4, maxLocals);
    }

    @Override
    public void visitCode() {
        this.switchLabels.clear();
        super.visitCode();
    }

    public static class BranchStruct{
        //unique for a branch
        int branchId=-1;
        //class#method
        String callsite="";
        //mark lines where it jumps to
        int[] wheres =new int[0];

        public BranchStruct(int branchId, String callsite, int[] wheres) {
            this.branchId = branchId;
            this.callsite = callsite;
            this.wheres = wheres;
        }
    }

}
