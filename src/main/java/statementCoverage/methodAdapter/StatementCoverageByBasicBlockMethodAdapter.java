package statementCoverage.methodAdapter;

import com.github.javaparser.utils.Pair;
import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import storage.Storage;
import utils.Tracer;

import java.lang.reflect.Field;
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
public class StatementCoverageByBasicBlockMethodAdapter extends MethodVisitor {
    Logger logger = Logger.getLogger(StatementCoverageByBasicBlockMethodAdapter.class);
    String name;

    Set<Label> handlers=new HashSet<>();
    Set<Label> ends=new HashSet<>();
    Set<Integer> lines=new HashSet<>();
    Domain domain=new Domain();
    int line=-1;
    boolean isFirst=true;

    /**
     * used to record basic block
     */
    private static class Domain{
        public static class Border{
            public int value;
            public boolean opened;
            Border(int value,boolean opened){
                this.value=value;
                this.opened=opened;
            }

            @Override
            public boolean equals(Object obj) {
                if(obj instanceof Border){
                    return ((Border)obj).value==this.value;
                }
                return false;
            }
        }
        public static class Range{
            public int left=-1;
            public int right=-1;
            Set<Integer> values=new HashSet<>();
        }
        public List<Border> borders=new ArrayList<>();
        public List<Range> ranges=new ArrayList<>();

        /*
         * N(R)=N(B)-1
         */
        public void initRanges(){
            int size=borders.size();
            if(size>=2){
                while(size>=2){
                    size-=1;
                    this.ranges.add(new Range());
                }
            }
        }

        /*
         * 确定Range范围
         */
        public void endRanges(){
            for(Range range:this.ranges){
                if(range.values.size()>0){
                    range.left=Collections.min(range.values);
                    range.right=Collections.max(range.values);
                }
            }
        }

        /*
         * add line to range corresponding to it
         */
        public void addLine(int line){
            int size=ranges.size();
            if(borders.size()==size+1){
                for(int i=0;i<size;i+=1){
                    Border left=borders.get(i);
                    Border right=borders.get(i+1);
                    boolean add= line >= left.value;
                    if(line==left.value&&!left.opened){
                        add=false;
                    }
                    if(line>right.value){
                        add=false;
                    }
                    if(line==right.value&&right.opened){
                        add=false;
                    }
                    if(add){
                        this.ranges.get(i).values.add(line);
                        break;
                    }
                }
            }

        }
    }

    protected StatementCoverageByBasicBlockMethodAdapter(MethodVisitor m, String n) {
        super(458752,m);
        name = n;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line,start);
        this.lines.add(line);
        this.line=line;
        //add border
        if(isFirst){
            this.domain.borders.add(new Domain.Border(line,true));
            isFirst=false;
        }
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        //add borders
        for(Label label:labels)try {
            Field line=label.getClass().getDeclaredField("lineNumber");
            line.setAccessible(true);
            this.domain.borders.add(new Domain.Border(line.getInt(label),true));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        //add borders
        for(Label label:labels)try {
            Field line=label.getClass().getDeclaredField("lineNumber");
            line.setAccessible(true);
            this.domain.borders.add(new Domain.Border(line.getInt(label),true));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        //if、goto指令
        //add borders
        try {
            Field line=label.getClass().getDeclaredField("lineNumber");
            line.setAccessible(true);
            this.domain.borders.add(new Domain.Border(line.getInt(label),true));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.domain.borders.add(new Domain.Border(this.line,false));
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        if((opcode>= Opcodes.IRETURN && opcode<=Opcodes.RETURN)||opcode==Opcodes.ATHROW){
            //return、throw指令
            //add borders
            this.domain.borders.add(new Domain.Border(this.line,false));
        }
    }

    /*
     * mark catch clause
     */
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        this.handlers.add(handler);
        this.ends.add(end);
    }

    /*
     * meet recorded exception handler/end,then record its lineNumber
     */
    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        //add borders
        if(this.handlers.contains(label)||this.ends.contains(label)){
            Field line;
            try {
                line = label.getClass().getDeclaredField("lineNumber");
                line.setAccessible(true);
                this.domain.borders.add(new Domain.Border(line.getInt(label),true));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        this.domain.borders.sort((o1, o2) -> Integer.compare(o1.value, o2.value));
        this.domain.initRanges();
        for(Integer line:lines){
            this.domain.addLine(line);
        }
        this.domain.endRanges();
        for (Domain.Range range:this.domain.ranges){
            if(range.left<=range.right&&range.left>0){
                insertProbe(range.left, range.right-range.left+1);
            }
        }

    }

    private void insertProbe(int left, int line){
        Tracer.executeLines(left,line,name);
        int i= Storage.lines.get();
        Storage.lines.set(i+line);
    }

    /**
     * after right probes have been all recorded
     * for the second time visitClass method called
     * probes should be inserted into bytecode
     * StatementCoverageMethodAdapterExecutor will modify bytecode to see
     * which line has been executed during runtime
     */
    public static class StatementCoverageMethodAdapterExecutor extends MethodVisitor{
        String name;
        boolean isTarget=false;
        List<Pair<Integer,Integer>> probes=null;

        protected StatementCoverageMethodAdapterExecutor(MethodVisitor m, String n) {
            super(458752,m);
            this.name=n;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            isTarget= Storage.probes.get().containsKey(this.name);
            probes= Storage.probes.get().get(this.name);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            if(isTarget){
                for(Pair<Integer,Integer> pair:probes){
                    if(line == (int) pair.a + pair.b - 1){
                        insertRightProbe(pair.b);
                        break;
                    }
                }
            }
        }

        private void insertRightProbe(int lines){
            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "utils/Tracer", "getInstance", "()L"
                            + "utils/Tracer" + ";");
            mv.visitLdcInsn(lines);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                    "utils/Tracer", "executeLines2",
                    "(I)V");
        }
    }
}
