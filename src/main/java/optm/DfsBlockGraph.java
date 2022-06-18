package optm;

import model.BasicBlock;

import java.util.*;

public class DfsBlockGraph {
    public Map<Integer, Set<Integer>> dfsParentToSons;
    public Map<Integer,Integer> dfsSonToParent;
    public Map<Integer,Set<Integer>> flowsSonParents;
    public Map<Integer,Set<Integer>> flowsParentSons;
    //public int[][] dfsTree;
    public int[] marksByVertex;
    public int[] vertexByMarks;
    public List<BasicBlock> blocks;
    public int[] semiDominators; //index-mark
    public int[] relativeDominators; //index-index
    public int[] immediateDominators; //index-mark
    //fixme for test
//    public List<Block> testBlocks;
//    public String method;

    private int findParentInDfs(int i){
        return dfsSonToParent.getOrDefault(i,-1);
    }

    public void sdoms(){
        if(semiDominators ==null){
            semiDominators =new int[marksByVertex.length];
        }
        //follow the order of reversed dfs exclude root block
        //root should be excluded
        for(int i = marksByVertex.length; i>1; i--){
            int index= vertexByMarks[i-1];
            if(index>0){
                semiDominators[index]= sdom(index);
            }
        }
    }

    private int sdom(int k){
        //check if sdom exists
        if(semiDominators[k]>0)return semiDominators[k];

        TreeSet<Integer> compareSet=new TreeSet<>();

        Set<Integer> parents;


        if((parents=flowsSonParents.get(k))!=null){
            for(int i:parents){
                //there is an edge v->w
                if(marksByVertex[i]< marksByVertex[k]){
                    //pre(v)<pre(w)
                    compareSet.add(marksByVertex[i]);
                }else{
                    //find sdom(u) when u>w and u is ancestor of v(include v itself)
                    int parent=i;
                    while(parent>=0&& marksByVertex[parent]> marksByVertex[k]){
                        compareSet.add(sdom(parent));
                        parent=findParentInDfs(parent);
                    }
                }
            }
        }

//        if(compareSet.size()==0){
//            System.out.println(0);
//        }
        return compareSet.first();
    }

    public void rdoms(){
        if(relativeDominators==null){
            relativeDominators =new int[marksByVertex.length];
        }
        //for every vertex v
        //find u on the path from sdom(v) to v while u has minimum sdom(u)
        for(int i = 1; i< marksByVertex.length; i++){
            if(marksByVertex[i]>1){
                //not a dangling block
                relativeDominators[i]=rdom(i);
            }
        }
    }

    private int rdom(int i){
        //check if rdom exists
        if(relativeDominators[i]>0){return relativeDominators[i];}
        int sdom= sdom(i);
        int index=vertexByMarks[sdom-1];
        int parent=i;

        int minimumSem=sdom;
        int minimumIndex=i;
        while(parent!=index){
            int currentSem= sdom(parent);
            if(currentSem<=minimumSem){
                minimumSem= sdom(parent);
                minimumIndex=parent;
            }
            parent=findParentInDfs(parent);
        }
        return minimumIndex;
    }

    public void idoms(){
        if(immediateDominators==null){
            immediateDominators =new int[marksByVertex.length];
        }
        for(int i = 1; i< marksByVertex.length; i++){
            if(marksByVertex[i]>1){
                //not a dangling block
                immediateDominators[i]=idom(i);
            }
        }
    }

    private int idom(int i){
        if(relativeDominators[i]==i){
            //idom(w)=sdom(w)
            return semiDominators[i];
        }else{
            return idom(relativeDominators[i]);
        }
    }

    public DominatorTreeGraph domTree(){
        DominatorTreeGraph dominatorTreeGraph=new DominatorTreeGraph();
        dominatorTreeGraph.blocks=this.blocks;
        dominatorTreeGraph.marks=marksByVertex;
        dominatorTreeGraph.flowsParentSons=this.flowsParentSons;
        dominatorTreeGraph.dominatorTree =new HashMap<>();
        dominatorTreeGraph.dominatorTreeReversed=new HashMap<>();

        for(int i=0;i<immediateDominators.length;i++){
            dominatorTreeGraph.dominatorTree.putIfAbsent(i,new HashSet<>());
        }

        //add tree edge
        for(int i=0;i<immediateDominators.length;i++){
            if(immediateDominators[i]>0){
                dominatorTreeGraph.dominatorTreeReversed.put(i,this.vertexByMarks[immediateDominators[i]-1]);
                dominatorTreeGraph.dominatorTree.get(this.vertexByMarks[immediateDominators[i]-1]).add(i);
            }
        }

        return dominatorTreeGraph;

    }
}
