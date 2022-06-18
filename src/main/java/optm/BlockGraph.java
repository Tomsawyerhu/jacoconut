package optm;

import model.BasicBlock;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;
import soot.toolkits.graph.Block;

import java.io.File;
import java.util.*;

public class BlockGraph {
    public String methodSig;
    public List<BasicBlock> blocks;
    public String linkingFile;
    public Map<Integer,Set<Integer>> flowsParentSons;
    public Map<Integer,Set<Integer>> flowsSonParents;
    //fixme for test
//    public List<Block> testBlocks;
//    public String method;

    public boolean isIn(int index){
        return index==0;
    }

    private int getIn(){
        for(int i:flowsParentSons.keySet()){
            if(isIn(i))return i;
        }
        return -1;
    }

    public boolean isExit(int index){
        if(index>flowsParentSons.size()-1){return false;}
        return flowsParentSons.get(index).size()==0;
    }

    public DfsBlockGraph dfsTreeGraph(){
        DfsBlockGraph graph=new DfsBlockGraph();
        graph.blocks=this.blocks;
        graph.dfsSonToParent=new HashMap<>();
        graph.dfsParentToSons =new HashMap<>();
        graph.marksByVertex =new int[blocks.size()];
        graph.vertexByMarks=new int[blocks.size()];
        graph.flowsSonParents=this.flowsSonParents;
        graph.flowsParentSons=this.flowsParentSons;
//        graph.testBlocks=this.testBlocks;
//        graph.method=this.method;

        int in=getIn();

        int mark=1;
        Set<Integer> visited=new HashSet<>();
        Stack<Integer> s=new Stack<>();
        int current=in;

        while(true){
            //visit current
            visited.add(current);
            graph.marksByVertex[current]=mark;
            graph.vertexByMarks[mark-1]=current;
            mark++;

            //find offspring
            int offspring=findFirstUnvisitedBlock(current,visited);

            if(offspring>=0){
                //offspring found
                graph.dfsParentToSons.putIfAbsent(current,new HashSet<>());
                graph.dfsParentToSons.get(current).add(offspring);
                graph.dfsSonToParent.put(offspring,current);
                s.push(current);
                current=offspring;

            }else{
                //no offspring found
                //find proper ancestor
                boolean ancestorFound=false;
                while(!s.isEmpty()){
                    int ancestor=s.pop();
                    int block=findFirstUnvisitedBlock(ancestor,visited);
                    if(block>=0){
                        //ancestor found
                        graph.dfsSonToParent.put(block,ancestor);
                        graph.dfsParentToSons.putIfAbsent(ancestor,new HashSet<>());
                        graph.dfsParentToSons.get(ancestor).add(block);
                        s.push(ancestor);
                        current=block;
                        ancestorFound=true;
                        break;
                    }
                }
                if(!ancestorFound){break;}
            }
        }

        return graph;
    }

    private int findFirstUnvisitedBlock(int current,Set<Integer> visited){
        int s=-1;
        for(int i=0;i<blocks.size();i++){
            if(current!=i&&flowsParentSons.get(current).contains(i)&&!visited.contains(i)){
                s=i;
                break;
            }
        }
        return s;
    }

    public static void main(String[] args) {
//        BlockGraph bg=new BlockGraph();
//        bg.flows=new int[][]{
//                new int[]{0,1,1,0,0,0,0,0,0,0},
//                new int[]{0,0,1,0,1,0,0,0,1,0},
//                new int[]{0,0,0,1,0,0,0,0,0,0},
//                new int[]{0,1,0,0,0,0,0,0,0,0},
//                new int[]{0,0,0,0,0,1,0,1,0,0},
//                new int[]{0,0,1,0,0,0,1,0,0,0},
//                new int[]{1,0,0,1,1,0,0,0,0,0},
//                new int[]{0,0,0,0,0,0,1,0,0,0},
//                new int[]{0,0,0,0,1,0,0,1,0,0},
//                new int[]{0,0,0,0,0,0,0,0,0,0},
//        };
//
//        bg.blocks=new ArrayList<>();
//        for(int i=0;i<10;i++){
//            bg.blocks.add(new BasicBlock());
//        }
//        DfsBlockGraph g=bg.dfsTreeGraph();
//        g.sdom();
//        g.rdoms();
//        g.idoms();
//
//        Visualizer.v().addGraph(bg);
//        Visualizer.v().draw();

        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(System.getProperty("user.dir") + File.separator + "target" + File.separator + "classes");

        Options.v().set_interactive_mode(true);
        Options.v().set_src_prec(Options.src_prec_only_class);
        Options.v().set_keep_line_number(true);

        SootClass sc=Scene.v().loadClassAndSupport("tests.FizzBuzz");
        Scene.v().loadNecessaryClasses();
        BlockGraph bg=CfgBuilder.buildBlockCfg(sc.getMethod("void printFizzBuzz(int)"));
        bg.linkingFile="C:\\Users\\tom\\Desktop\\jacoconut\\src\\main\\java\\tests\\FizzBuzz.java";
        Visualizer.v().addGraph(bg);
        Visualizer.v().draw();
    }


}
