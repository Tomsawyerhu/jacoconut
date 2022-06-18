package optm;

import model.BasicBlock;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DominatorTreeGraph {

    public Map<Integer,Set<Integer>> dominatorTree;
    public Map<Integer,Integer> dominatorTreeReversed;
    public Map<Integer,Set<Integer>> flowsParentSons;
    public int[] marks;
    public List<BasicBlock> blocks;

    public Set<Integer> leafNodes(){
       return dominatorTree.keySet().stream().filter(key -> dominatorTree.get(key).size()==0).collect(Collectors.toSet());
    }

    public Set<Integer> internalNodes(){
        return dominatorTree.keySet().stream().filter(key -> dominatorTree.get(key).size()>0).collect(Collectors.toSet());
    }


    public Set<Integer> internalNeedInstrumentNodes(){
        Set<Integer> internalNodes=internalNodes();
        Set<Integer> result=new HashSet<>();
        for(int internalNode:internalNodes){
            for(int i:flowsParentSons.get(internalNode)){
                if(!dominate(internalNode,i)){
                    result.add(i);
                }
            }
        }
        return result;
    }

    private boolean dominate(int i,int j){
        return dominators(j).contains(i);
    }


    public Set<Integer> dominators(int index){
        Set<Integer> results=new HashSet<>();
        int par=index;
        while((par=parent(par))>=0){
            results.add(par);
        }
        return results;
    }

    private int parent(int index){
        return dominatorTreeReversed.getOrDefault(index,-1);
    }
}
