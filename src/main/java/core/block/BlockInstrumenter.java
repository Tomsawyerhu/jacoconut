package core.block;

import core.edge.EdgeInstrumenter;
import model.BasicBlock;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.util.Chain;
import storage.Storage;

import java.util.*;

public class BlockInstrumenter extends BodyTransformer {
    private static final SootClass counterClass;
    private static final SootMethod executeBlock;
    private static Integer blockId=0;
    private static final Logger logger=Logger.getLogger(BlockInstrumenter.class);

    static {
        counterClass= Scene.v().loadClassAndSupport("externX.JacoconutX");
        executeBlock =counterClass.getMethod("void executeBlock(int)");
    }

    private static int allocateBlockId(){
        synchronized (BlockInstrumenterAfterOptm.class){
            int current=blockId;
            blockId++;
            return current;
        }
    }

    private static int[] allocateBlockId(int num){
        synchronized (BlockInstrumenter.class){
            int[] results=new int[num];
            for(int i=0;i<num;i++){
                results[i]=blockId+i;
            }
            blockId+=num;
            return results;
        }
    }

    @Override
    protected void internalTransform(Body body, String s, Map map) {
        SootMethod method = body.getMethod();
        String declaringClass = method.getDeclaringClass().getName();
        String sig = method.getSignature();
        logger.info("start "+sig);
        String key = declaringClass + "#" + sig;
        Storage.blocks.get().putIfAbsent(key, new HashSet<>());

        BlockGraph blockGraph = new CompleteBlockGraph(body);
        List<Block> blocks = blockGraph.getBlocks();
        //allocate blockId for each block

        int[] blockIds=allocateBlockId(blocks.size());
        for (Block b : blocks) {
            int blockId=blockIds[b.getIndexInMethod()];
            //build basic block
            BasicBlock bb=BasicBlock.transform(b);
            bb.blockId=blockId;

            Storage.blocks.get().get(key).add(bb);
        }

        Chain<Unit> units = body.getUnits();
        Iterator<Unit> stmtIt = units.snapshotIterator();
        while (stmtIt.hasNext()) {
            Unit unit = stmtIt.next();
            Stmt stmt = (Stmt) unit;
            for (Block b : blocks) {
                if (b.getTail() == unit) {
                    InvokeExpr incExpr = Jimple.v().newStaticInvokeExpr(executeBlock.makeRef(), IntConstant.v(blockIds[b.getIndexInMethod()]));
                    Stmt incStmt = Jimple.v().newInvokeStmt(incExpr);
                    units.insertBefore(incStmt, stmt);
                }
            }
        }
    }
}
