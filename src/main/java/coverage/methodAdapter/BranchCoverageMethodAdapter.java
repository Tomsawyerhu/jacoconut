package coverage.methodAdapter;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import storage.StorageHandler;

import java.util.*;

public class BranchCoverageMethodAdapter extends MethodVisitor {
    private static Logger logger = Logger.getLogger(BranchCoverageMethodAdapter.class);
    private final String className;
    private final String methodName;
    private int line=-1;

    private Map<Label,Integer> switchLabels = new HashMap<>();
    private static int branchId=0;
    public List<BranchStruct> branchList=new ArrayList<>();

    public BranchCoverageMethodAdapter(MethodVisitor mv,String n1,String n2) {
        super(458752,mv);
        className=n1;
        methodName=n2;
    }

    @Override
    public void visitLabel(Label label) {
        String callsite=className+"#"+methodName;

        if (switchLabels.containsKey(label)) {
            this.branchList.add(new BranchStruct(switchLabels.get(label),callsite,this.line,2));
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "externX/JacoconutX", "getInstance", "()L"
                            + "externX/JacoconutX" + ";");
            mv.visitLdcInsn(switchLabels.get(label));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "externX/JacoconutX", "executeBranch",
                    "(I)V");
        }
        super.visitLabel(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String callsite=this.className+"#"+this.methodName;

        if (opcode == org.objectweb.asm.Opcodes.GOTO) {
            //忽略goto
            mv.visitJumpInsn(opcode, label);
            return;
        }

        //true分支
        int trueId=branchId;
        branchList.add(new BranchStruct(trueId,callsite,this.line,0));
        //false分支
        int falseId=++branchId;
        branchList.add(new BranchStruct(falseId,callsite,this.line,1));
        branchId++;

        //修改字节码
        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "externX/JacoconutX", "getInstance", "()L"
                        + "externX/JacoconutX" + ";");
        mv.visitLdcInsn(falseId);
        mv.visitLdcInsn(false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "externX/JacoconutX", "executeBranch",
                "(IZ)V");

        mv.visitJumpInsn(opcode, label);

        this.visitMethodInsn(Opcodes.INVOKESTATIC,
                "externX/JacoconutX", "getInstance", "()L"
                        + "externX/JacoconutX" + ";");
        mv.visitLdcInsn(trueId);
        mv.visitLdcInsn(true);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "externX/JacoconutX", "executeBranch",
                "(IZ)V");
    }


    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        for(Label label:labels){
            this.switchLabels.put(label, branchId++);
        }
        switchLabels.remove(dflt);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        for(Label label:labels){
            this.switchLabels.put(label, branchId++);
        }
        switchLabels.remove(dflt);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        this.line=line;
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitCode() {
        this.switchLabels.clear();
        super.visitCode();
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        StorageHandler.setBranch(className+"#"+methodName,branchList);
    }

    public static class BranchStruct{
        //unique for a branch
        int branchId;
        //class#method
        String callsite;
        //mark lines where it jumps to
        int lineNum;
        //type(0 for if true,1 for if false,2 for switch)
        int type;

        public BranchStruct(int branchId, String callsite, int lineNum, int type) {
            this.branchId = branchId;
            this.callsite = callsite;
            this.lineNum = lineNum;
            this.type = type;
        }

        public int id(){return branchId;}

        public int lineNum(){return lineNum;}

        public String type(){
            if(type==0){return "IF TRUE";}
            else if(type==1){
                return "IF FALSE";
            }else if(type==2){
                return "SWITCH";
            }else{
                return "UNKNOWN";
            }
        }
    }

}
