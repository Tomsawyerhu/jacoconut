package coverage.methodAdapter;

import com.github.javaparser.utils.Pair;
import model.BasicBlock;
import model.ControlFlowGraph;
import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import storage.Storage;
import java.util.*;

/**
 * 行覆盖
 * 非顺序语句、分支插入探针
 * 左探针：区间左边界
 * 右探针：区间右边界
 * 探针：左探针+右探针
 * 第一次迭代：
 *   1.1 记录Basic Block边界行数构建Border对象存储在Domain中，边界开闭( opened为true代表左开右闭，e.g. (true,false)=[) )
 *   1.2 记录所有行数
 *   1.3 根据Border初始化Range(N(R)=N(B)-1)
 *   1.4 将代码行插入Domain的Range中
 *   1.5 计算出Range范围
 *   1.6 生成Probe并存储
 * 第二次迭代：修改字节码
 *
 * asm visit order:
 * A visitor to visit a Java method. The methods of this class must be called in the following order:
 * ( visitParameter )*
 * [ visitAnnotationDefault ]
 * ( visitAnnotation | visitAnnotableParameterCount | visitParameterAnnotation visitTypeAnnotation | visitAttribute )*
 * [ visitCode ( visitFrame | visit<i>X</i>Insn | visitLabel | visitInsnAnnotation | visitTryCatchBlock | visitTryCatchAnnotation | visitLocalVariable | visitLocalVariableAnnotation | visitLineNumber )* visitMaxs ]
 * visitEnd
 * In addition, the visit<i>X</i>Insn and visitLabel methods must be called in the sequential order of the bytecode instructions of the visited code,
 * visitInsnAnnotation must be called after the annotated instruction,
 * visitTryCatchBlock must be called before the labels passed as arguments have been visited,
 * visitTryCatchBlockAnnotation must be called after the corresponding try catch block has been visited,
 * and the visitLocalVariable, visitLocalVariableAnnotation and visitLineNumber methods must be called after the labels passed as arguments have been visited.
 */
public class CfgMethodAdapter extends MethodVisitor {
    Logger logger = Logger.getLogger(CfgMethodAdapter.class);
    String name;
    String className;

    static private int blockId=0;
    Label current;
    List<Label> labelList=new ArrayList<>();
    List<Pair<Label,Label>> flows=new ArrayList<>();
    List<Boolean> flowCondition=new ArrayList<>();


    protected CfgMethodAdapter(MethodVisitor m, String n1, String n2) {
        super(458752,m);
        name = n2;
        this.className=n1;
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
        for(Label l:labels){
            flows.add(new Pair<>(current,l));
            flowCondition.add(true);
        }
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        for(Label l:labels){
            flows.add(new Pair<>(current,l));
            flowCondition.add(true);
        }
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.flows.add(new Pair<>(current,label));
        flowCondition.add(opcode!=org.objectweb.asm.Opcodes.GOTO);
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        current=label;
        this.labelList.add(label);
        super.visitLabel(label);
    }

    @Override
    public void visitEnd() {
        int[][] flows=new int[labelList.size()][labelList.size()];
        for(int i=0;i<this.flows.size();i+=1){
            Pair<Label, Label> p=this.flows.get(i);
            int src=labelList.indexOf(p.a);
            int dest=labelList.indexOf(p.b);
            flows[src][dest]=this.flowCondition.get(i)?1:2; //条件分支填1，非条件分支填2
        }
        //条件跳转，顺序label之间存在指向关系
        for(int i=0;i<labelList.size();i+=1){
            boolean noUnconditionalJump=false;
            for(int j=0;j<labelList.size();j+=1){
                if(flows[i][j]==2&&i!=j){noUnconditionalJump=true;break;} //如果存在非条件自环，说明为条件跳转(由上下文决定)
            }
            if(!noUnconditionalJump&&i<labelList.size()-1&& flows[i][i+1]==0){
                flows[i][i+1]=1;
            }
        }
        //将所有非条件跳转的2改为1
        for(int i=0;i<labelList.size();i+=1){
            for(int j=0;j<labelList.size();j+=1){
                if(flows[i][j]==2){flows[i][j]=1;}
            }
        }

        ControlFlowGraph cfg=new ControlFlowGraph(className,name);
        cfg.flows=flows;
        List<BasicBlock> bbs=new ArrayList<>();
        for(int i=0;i<this.labelList.size();i+=1){
            bbs.add(labelToBb(labelList.get(i),i));
        }
        cfg.bbs=bbs;
        Storage.cfgs.get().add(cfg);
        super.visitEnd();
    }

    private static BasicBlock labelToBb(Label label,int index){
        BasicBlock bb=new BasicBlock();
        bb.blockId=blockId;
        blockId+=1;
        bb.startLabel=index;
        bb.labelNum=1;
        return bb;
    }


    public static class StartEndMethodAdapter extends MethodVisitor{
        String className;
        String name;
        private boolean isFirst=true;

        public StartEndMethodAdapter(MethodVisitor m, String n1, String n2) {
            super(458752,m);
            this.className=n1;
            this.name=n2;
        }

        @Override
        public void visitLabel(Label label) {
            if(isFirst){
                this.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "externX/JacoconutX", "getInstance", "()L"
                                + "externX/JacoconutX" + ";");
                mv.visitLdcInsn(className+"#"+name);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        "externX/JacoconutX", "methodStart",
                        "(Ljava/lang/String;)V");
                isFirst=false;
            }
            super.visitLabel(label);
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
                        "externX/JacoconutX", "methodEnd",
                        "(Ljava/lang/String;)V");
            }
            super.visitInsn(opcode);
        }
    }




}
