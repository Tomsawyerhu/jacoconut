package my;

import com.sun.xml.internal.ws.org.objectweb.asm.Label;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodAdapter;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import coverage.ECGCoverageListener;
import coverage.Tracer;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 行覆盖
 * 非顺序语句、分支插入探针
 * 左探针：顺序执行代码块起始行
 *   1. 左探针为空
 * 右探针：顺序执行代码块结束行
 *   1. 第一遍记录右探针位置，第二遍修改class文件
 *   2.1 遍历
 *       2.1.1 athrow、return之前
 *       2.1.2 分支跳转语句记录跳转位置，等到visitlinenumber到这个位置的时候把上一次遍历到的行作为右探针位置进行记录
 */
public class StatementCoverageMethodAdapter extends MethodAdapter {
    Logger logger = Logger.getLogger(coverage.StatementCoverageMethodAdapter.class);
    String name;

    int line;
    int left=-1;
    Set<Integer> possibleLefts=new HashSet<>();


    public StatementCoverageMethodAdapter(MethodVisitor m, String n) {
        super(m);
        name = n;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);

        if(possibleLefts.contains(line)&&left>0){
            //todo when reach the line where left probe should be inserted while last left probe exists,insert right probe
            insertRightProbe();
        }
        this.line=line;
        if(left==-1){
            left=line;
        }
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        //todo record where to insert left probe
        for(Label label:labels)this.possibleLefts.add(label.getOffset());
        //todo insert right probe
        insertRightProbe();
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        //todo record where to insert left probe
        for(Label label:labels)try {
            Field line=label.getClass().getDeclaredField("line");
            line.setAccessible(true);
            this.possibleLefts.add(line.getInt(label));
            this.possibleLefts.add(this.line+1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //todo insert right probe
        insertRightProbe();
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        //if、goto指令
        //todo record where to insert left probe
        try {
            Field line=label.getClass().getDeclaredField("line");
            line.setAccessible(true);
            this.possibleLefts.add(line.getInt(label));
            this.possibleLefts.add(this.line+1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //todo insert right probe
        insertRightProbe();
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        if((opcode>= Opcodes.IRETURN && opcode<=Opcodes.RETURN)||opcode==Opcodes.ATHROW){
            //return、throw指令
            //todo insert right probe
            insertRightProbe();
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    private void insertRightProbe(){
//        this.visitMethodInsn(Opcodes.INVOKESTATIC,
//                "coverage/Tracer", "getInstance", "()L"
//                        + "coverage/Tracer" + ";");
//        mv.visitLdcInsn(String.valueOf(left));
//        mv.visitLdcInsn(String.valueOf(line-left+1));
//        mv.visitLdcInsn(name);
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                "coverage/Tracer", "executeLines",
//                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
        Tracer.executeLines(left,line-left+1,name);

        logger.info("left:"+left+";right:"+this.line);
        int i=ECGCoverageListener.lines.get();
        ECGCoverageListener.lines.compareAndSet(i,i+this.line-left+1);
        left=-1;
    }

    public static class StatementCoverageMethodAdapterExecutor extends MethodAdapter{
        String name;
        boolean isTarget=false;
        List<Pair<Integer,Integer>> probes=null;

        public StatementCoverageMethodAdapterExecutor(MethodVisitor m, String n) {
            super(m);
            this.name=n;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            isTarget=ECGCoverageListener.probes.get().containsKey(this.name);
            probes=ECGCoverageListener.probes.get().get(this.name);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            if(isTarget){
                for(Pair<Integer,Integer> pair:probes){
                    if(line == (int) pair.getKey() + pair.getValue() - 1){
                        insertRightProbe(pair.getValue());
                    }
                }
            }
        }

        private void insertRightProbe(int lines){
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "coverage/Tracer", "getInstance", "()L"
                            + "coverage/Tracer" + ";");
            mv.visitLdcInsn(lines);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "coverage/Tracer", "executeLines2",
                    "(I)V");
        }
    }
}
