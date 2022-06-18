package model;

import soot.toolkits.graph.Block;

public class Edge {
    public BasicBlock start;
    public BasicBlock end;
    public int edgeId;

    public static Edge transform(Block b1,Block b2,int id1,int id2){
        Edge edge=new Edge();
        edge.start=BasicBlock.transform(b1);
        edge.start.blockId=id1;
        edge.end=BasicBlock.transform(b2);
        edge.end.blockId=id2;
        return edge;
    }
}
