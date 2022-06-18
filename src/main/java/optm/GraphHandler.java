package optm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphHandler {
    public static Map<Integer, Set<Integer>> calculateNecessaryInstrumentedBlocks(DominatorTreeGraph dtg){
        Map<Integer, Set<Integer>> result=new HashMap<>();
        Set<Integer> leafNodes=dtg.leafNodes();
        Set<Integer> others=dtg.internalNeedInstrumentNodes();
        for(int leafNode:leafNodes){
            Set<Integer> dominators= dtg.dominators(leafNode);
            dominators.removeAll(others);
            dominators.add(leafNode);
            result.put(leafNode,dominators);
        }
        for(int node:others){
            result.put(node, Collections.singleton(node));
        }
        return result;
    }
}
