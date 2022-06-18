package core.block;

import model.BasicBlock;
import optm.*;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.util.Chain;
import storage.Storage;

import java.util.*;

public class BlockInstrumenterAfterOptm extends BodyTransformer {
    private static final SootClass counterClass;
    private static final SootMethod executeBlocks;
    private static Integer blockId=0;
    private static final Logger logger=Logger.getLogger(BlockInstrumenterAfterOptm.class);

    static {
        counterClass= Scene.v().loadClassAndSupport("externX.JacoconutX");
        executeBlocks =counterClass.getMethod("void executeBlock(java.lang.String)");
    }

    private static int allocateBlockId(){
        synchronized (BlockInstrumenterAfterOptm.class){
            int current=blockId;
            blockId++;
            return current;
        }
    }

    @Override
    protected void internalTransform(Body body, String s, Map map) {
        //calculate necessary instrumented block
        SootMethod method=body.getMethod();
        BlockGraph myBlockGraph= CfgBuilder.buildBlockCfg(method);
        //System.out.println("size:"+myBlockGraph.flowsParentSons.size());
        DfsBlockGraph dfsBlockGraph=myBlockGraph.dfsTreeGraph();
        dfsBlockGraph.sdoms();
        dfsBlockGraph.rdoms();
        dfsBlockGraph.idoms();
        DominatorTreeGraph dominatorTreeGraph=dfsBlockGraph.domTree();
        Map<Integer, Set<Integer>> relations=GraphHandler.calculateNecessaryInstrumentedBlocks(dominatorTreeGraph);
        BlockCounter.count(relations.size());
        BlockCounter.count1(relations.values().stream().reduce((integers, integers2) -> {
            Set<Integer> reuslt=new HashSet<>();
            reuslt.addAll(integers);
            reuslt.addAll(integers2);
            return reuslt;
        }).get().size());
        BlockCounter.count2(myBlockGraph.blocks.size());

        String declaringClass = method.getDeclaringClass().getName();
        String sig = method.getSignature();
        logger.info("start "+sig);
        String key = declaringClass + "#" + sig;
        Storage.blocks.get().putIfAbsent(key, new HashSet<>());

        soot.toolkits.graph.BlockGraph blockGraph = new CompleteBlockGraph(body);
        List<Block> blocks = blockGraph.getBlocks();
        //allocate blockId for each block
        int[] blockIds=new int[blocks.size()];
        for (Block b : blocks) {
            int index=b.getIndexInMethod();
            blockIds[index]=allocateBlockId();

            //build basic block
            BasicBlock bb=BasicBlock.transform(b);
            bb.blockId=blockIds[index];

            Storage.blocks.get().get(key).add(bb);
        }

        Chain<Unit> units = body.getUnits();
        Iterator<Unit> stmtIt = units.snapshotIterator();
        while (stmtIt.hasNext()) {
            Unit unit = stmtIt.next();
            Stmt stmt = (Stmt) unit;
            for (Block b : blocks) {
                if (b.getTail() == unit&& relations.containsKey(b.getIndexInMethod())) {
                    List<Integer> indexes=new ArrayList<>(relations.get(b.getIndexInMethod()));
                    String ids=blockIds[indexes.get(0)]+"";
                    for(int i=1;i<indexes.size();i++){
                        ids+=",";
                        ids+=blockIds[indexes.get(i)];
                    }
                    //logger.info(ids);

                    InvokeExpr incExpr = Jimple.v().newStaticInvokeExpr(executeBlocks.makeRef(), StringConstant.v(ids));
                    Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
                    units.insertBefore(incStmt, stmt);
                }
            }
        }
        //logger.info("end "+sig);
    }
}
