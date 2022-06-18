package optm;

import model.BasicBlock;
import soot.*;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CfgBuilder {
    public static optm.BlockGraph buildBlockCfg(SootMethod method){
        JimpleBody body = (JimpleBody) method.retrieveActiveBody();
        optm.BlockGraph myBlockGraph=new optm.BlockGraph();
        myBlockGraph.methodSig=method.getSignature();

        //建立块控制流图
        BlockGraph blockGraph=new CompleteBlockGraph(body);
        List<Block> blocks=blockGraph.getBlocks();
        //fixme
//        myBlockGraph.testBlocks=blocks;
//        myBlockGraph.method=method.getName();

        myBlockGraph.blocks=new ArrayList<>();
        myBlockGraph.flowsParentSons=new HashMap<>();
        myBlockGraph.flowsSonParents=new HashMap<>();
//        myBlockGraph.testBlocks=blocks;
//        myBlockGraph.method=method.getSignature();

        //记录块的起始行和终止行
        for(Block block:blocks){
            Unit head=block.getHead();
            int headLine=Integer.parseInt(
                    head.getTag("LineNumberTag")==null?"-1":
                            head.getTag("LineNumberTag").toString());
            Unit tail=block.getTail();
            int tailLine=Integer.parseInt(
                    tail.getTag("LineNumberTag")==null?
                            "-1":tail.getTag("LineNumberTag").toString());

            BasicBlock bb=new BasicBlock();
            bb.headLine=headLine;
            bb.tailLine=tailLine;
            myBlockGraph.blocks.add(bb);

            //记录流向关系
            myBlockGraph.flowsParentSons.putIfAbsent(block.getIndexInMethod(),new HashSet<>());
            for(Block succ:block.getSuccs()){
                myBlockGraph.flowsParentSons.get(block.getIndexInMethod()).add(succ.getIndexInMethod());
                myBlockGraph.flowsSonParents.putIfAbsent(succ.getIndexInMethod(),new HashSet<>());
                myBlockGraph.flowsSonParents.get(succ.getIndexInMethod()).add(block.getIndexInMethod());
            }
        }
        return myBlockGraph;
    }
}
