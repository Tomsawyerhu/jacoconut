package core.edge;

import core.block.BlockInstrumenterAfterOptm;
import model.Edge;
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

public class EdgeInstrumenter extends BodyTransformer {
    private static final SootClass counterClass;
    private static final SootMethod executeBlock;
    private static Integer blockId=0;
    private static Integer edgeId=0;
    private static final Logger logger=Logger.getLogger(EdgeInstrumenter.class);

    static {
        counterClass= Scene.v().loadClassAndSupport("externX.JacoconutX");
        executeBlock =counterClass.getMethod("void executeBlock(int)");
    }

    private static int[] allocateBlockId(int num){
        synchronized (EdgeInstrumenter.class){
            int[] results=new int[num];
            for(int i=0;i<num;i++){
                results[i]=blockId+i;
            }
            blockId+=num;
            return results;
        }
    }

    private static int allocateEdgeId(){
        synchronized (EdgeInstrumenter.class){
            int current=edgeId;
            edgeId++;
            return current;
        }
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        SootMethod method = body.getMethod();
        String declaringClass = method.getDeclaringClass().getName();
        String sig = method.getSignature();
        logger.info("start "+sig);
        String key = declaringClass + "#" + sig;
        Storage.edges.get().putIfAbsent(key,new HashSet<>());

        BlockGraph blockGraph = new CompleteBlockGraph(body);
        List<Block> blocks = blockGraph.getBlocks();
        //allocate blockId for each block
        int[] blockIds=allocateBlockId(blocks.size());

        for(Block block:blocks){
            if(block.getSuccs()==null)continue;
            for(Block b:block.getSuccs()){
                int edgeId=allocateEdgeId();
                Edge edge=Edge.transform(block,b,blockIds[block.getIndexInMethod()],blockIds[b.getIndexInMethod()]);
                edge.edgeId=edgeId;

                Storage.edges.get().get(key).add(edge);
            }
        }

        //do same thing as BlockInstrumenter
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
