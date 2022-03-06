package coverage.methodAdapter;

import com.github.javaparser.utils.Pair;
import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import storage.Storage;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class PathCoverageMethodAdapter {
    public static class CfgMethodAdapter extends MethodVisitor {
        private static Logger logger = Logger.getLogger(NaiveStatementCoverageMethodAdapter.class);
        private static int blockId=0;
        String className;
        String name;

        private Map<Label,Integer> labelLines=new HashMap<>(); //label line对应关系

        private Label lastLabel;
        private int line; //代码行数
        private List<Flow> bbFlows =new ArrayList<>(); //基本块流向关系
        private List<LabelSequence> labelSequences =new ArrayList<>();//顺序关系
        private int headLine; //程序进入点行数
        ControlFlowGraph controlFlowGraph;

        boolean isFirst=true;

        public CfgMethodAdapter(MethodVisitor methodVisitor, String n1,String n2) {
            super(458752, methodVisitor);
            className=n1;
            name = n2;
            controlFlowGraph=new ControlFlowGraph(n1,n2);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            this.line=line;
            //add border
            if(isFirst){
                this.headLine=line;
                isFirst=false;
            }

            super.visitLineNumber(line,start);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            //非条件分支 must=true
            //条件分支 must=false

            bbFlows.add(new Flow(this.line,label,opcode==org.objectweb.asm.Opcodes.GOTO));
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            for(Label label:labels){
                bbFlows.add(new Flow(this.line,label,false));
            }
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            for(Label label:labels){
                bbFlows.add(new Flow(this.line,label,false));
            }
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLabel(Label label) {
            super.visitLabel(label);
            Field line= null;
            try {
                //label对应行数
                line = label.getClass().getDeclaredField("lineNumber");
                line.setAccessible(true);
                labelLines.putIfAbsent(label,line.getInt(label)==0?this.line:line.getInt(label));
                //label顺序关系
                if(line.getInt(label)!=0){
                    int lastLabelLine=labelLines.getOrDefault(lastLabel,-1);
                    if(lastLabelLine>0){
                        labelSequences.add(new LabelSequence(lastLabelLine,line.getInt(label)));
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            lastLabel=label;
        }

        @Override
        public void visitEnd() {
            for(Flow f:bbFlows){
                f.end = labelLines.getOrDefault((Label) f.end, -1);
            }
            String classMethod=this.className+"#"+this.name;
            List<Pair<Integer,Integer>> probes= Storage.probes.get().get(classMethod);
            if(probes==null){super.visitEnd();return;}
            List<BasicBlock> bbs=
                    probes
                    .stream()
                    .map(CfgMethodAdapter::probeToBb)
                    .sorted(Comparator.comparingInt(BasicBlock::getStartline))
                            .collect(Collectors.toList());



            //至少包含return语句，bbsize is positive
            int bbsize=bbs.size();
            int[][] flows=new int[bbsize][bbsize];

            if(bbsize>1){
                //将labelSequences中的行数替换为blockId
                for(LabelSequence labelSequence:labelSequences){
                    boolean startSolved=false;
                    boolean endSolved=false;
                    for(int i=0;i<bbsize-1;i+=1){
                        if(!startSolved&&labelSequence.start>=bbs.get(i).startline&&labelSequence.start<bbs.get(i+1).startline){
                            labelSequence.start=bbs.get(i).blockId;
                            startSolved=true;
                        }
                        if(!endSolved&& labelSequence.end >=bbs.get(i).startline&&labelSequence.end<bbs.get(i+1).startline){
                            labelSequence.end=bbs.get(i).blockId;
                            endSolved=true;
                        }
                    }
                    if(!startSolved){
                        labelSequence.start=bbs.get(bbsize-1).blockId;
                    }
                    if(!endSolved){
                        labelSequence.end=bbs.get(bbsize-1).blockId;
                    }
                }
                //将flow中的行数替换为blockId
                for(Flow flow: bbFlows){
                    boolean startSolved=false;
                    boolean endSolved=false;
                    for(int i=0;i<bbsize-1;i+=1){
                        if(!startSolved&&flow.start>=bbs.get(i).startline&&flow.start<bbs.get(i+1).startline){
                            flow.setStart(bbs.get(i).blockId);
                            startSolved=true;
                        }
                        if(!endSolved&&(int)flow.end>=bbs.get(i).startline&&(int)flow.end<bbs.get(i+1).startline){
                            flow.setEnd(bbs.get(i).blockId);
                            endSolved=true;
                        }
                    }
                    if(!startSolved){
                        flow.setStart(bbs.get(bbsize-1).blockId);
                    }
                    if(!endSolved){
                        flow.setEnd(bbs.get(bbsize-1).blockId);
                    }
                }

                //将流向关系填充到矩阵中
                for(int i=0;i<bbsize;i+=1){
                    for(int j=0;j<bbsize;j+=1){
                        for(Flow flow: bbFlows){
                            if(flow.start==bbs.get(i).blockId&&(int)flow.end==bbs.get(j).blockId){
                                if(flow.must&&flows[i][j]==0)flows[i][j]=2; // 非条件跳转(当非条件跳转与条件跳转并存时，视为条件跳转(由上下文决定)。e.g. ?:操作符)
                                else flows[i][j]=1; // 条件跳转
                            }
                        }
                    }
                }

                //条件跳转，顺序label之间存在指向关系
                for(int i=0;i<bbsize;i+=1){
                    boolean noUnconditionalJump=false;
                    for(int j=0;j<bbsize;j+=1){
                        if(flows[i][j]==2&&i!=j){noUnconditionalJump=true;break;} //如果存在非条件自环，说明为条件跳转(由上下文决定)
                    }
                    if(!noUnconditionalJump){
                        for(LabelSequence labelSequence:labelSequences){
                            if(labelSequence.start==bbs.get(i).blockId){
                                for(int j=0;j<bbsize-1;j+=1){
                                    if(labelSequence.end==bbs.get(j).blockId&&i!=j){
                                        flows[i][j]=1;
                                    }
                                }
                            }
                        }
                    }
                }

                //将所有非条件跳转的2改为1
                for(int i=0;i<bbsize;i+=1){
                    for(int j=0;j<bbsize;j+=1){
                        if(flows[i][j]==2){flows[i][j]=1;}
                    }
                }
            }

            this.controlFlowGraph.bbs= bbs;
            this.controlFlowGraph.flows=flows;

            Storage.cfgs.get().add(this.controlFlowGraph);

            super.visitEnd();
        }

        private static BasicBlock probeToBb(Pair<Integer,Integer> probe){
            BasicBlock bb=new BasicBlock();
            bb.blockId=blockId;
            blockId+=1;
            bb.startline=probe.a;
            bb.lines=probe.b;
            return bb;
        }

        public static class BasicBlock{
            public int blockId;
            public int startline;
            public int lines;

            private BasicBlock(){}

            public int getStartline() {
                return startline;
            }
        }

        public static class LabelSequence{
            public int start;
            public int end;

            public LabelSequence(int start, int end) {
                this.start = start;
                this.end = end;
            }
        }

        public static class ControlFlowGraph{
            public String className;
            public String methodName;

            public ControlFlowGraph(String className, String methodName) {
                this.className = className;
                this.methodName = methodName;
            }
            public List<BasicBlock> bbs;
            public int[][] flows;
        }

        private static class Flow{
            int start;
            Object end;
            boolean must;

            public Flow(int start, Object end, boolean must) {
                this.start = start;
                this.end = end;
                this.must = must;
            }

            public void setStart(int start) {
                this.start = start;
            }

            public void setEnd(Object end) {
                this.end = end;
            }
        }

    }
}
