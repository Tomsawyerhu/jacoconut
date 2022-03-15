package coverage.methodAdapter;

import com.github.javaparser.utils.Pair;
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

    private Map<Label,Pair<Integer,Integer>> switchLabels = new HashMap<>();
    private static int branchId=0;
    public List<BranchStruct> branchList=new ArrayList<>();
    private Map<Label,Integer> labelLine=new HashMap<>();

    public BranchCoverageMethodAdapter(MethodVisitor mv,String n1,String n2) {
        super(458752,mv);
        className=n1;
        methodName=n2;
    }

    @Override
    public void visitLabel(Label label) {
        this.labelLine.put(label,this.line);
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

        if (opcode == org.objectweb.asm.Opcodes.GOTO) {
            //忽略goto
            mv.visitJumpInsn(opcode, label);
            return;
        }

        //收集分支
        branchId+=1;
        branchList.add(new BranchStruct(branchId,callsite,new int[]{this.line,-1},new Label[]{null,label},this.line,0));


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
            this.switchLabels.put(label, new Pair<>(branchId, which));
            which+=1;
        }
        switchLabels.remove(dflt);
        branchList.add(new BranchStruct(branchId,callsite,wheres,labels,this.line,1));

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        String callsite=this.className+"#"+this.methodName;
        branchId+=1;
        int which=0;
        int[] wheres=new int[labels.length];
        for(Label label:labels){

            this.switchLabels.put(label, new Pair<>(branchId, which));
            which+=1;
        }
        switchLabels.remove(dflt);
        branchList.add(new BranchStruct(branchId,callsite,wheres,labels,this.line,1));
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

    @Override
    public void visitEnd() {
        super.visitEnd();
        for(BranchStruct bs:this.branchList){
            for(int i=0;i<bs.whereLabels.length;i+=1){
                bs.wheres[i]=labelLine.getOrDefault(bs.whereLabels[i],bs.wheres[i]);
            }
        }
        StorageHandler.setBranch(className+"#"+methodName,branchList);
    }

    public static class BranchStruct{
        //unique for a branch
        int branchId;
        //class#method
        String callsite;
        //mark lines where it jumps to
        int[] wheres;
        //mark labels where it jumps to
        Label[] whereLabels;
        //start
        int start;
        //type(0 for if,1 for switch)
        int type;

        public BranchStruct(int branchId, String callsite, int[] wheres,Label[] whereLabels,int start,int type) {
            this.branchId = branchId;
            this.callsite = callsite;
            this.wheres=wheres;
            this.whereLabels=whereLabels;
            this.start=start;
            this.type=type;
        }

        public int[] wheres(){return wheres;}

        public int id(){return branchId;}

        public int start(){return start;}

        public int size(){
            return wheres.length;
        }

        public String type(){
            if(type==0){return "IF";}
            else if(type==1){
                return "SWITCH";
            }else{
                return "UNKNOWN";
            }
        }
    }

}
