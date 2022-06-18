package model;

import soot.Unit;
import soot.toolkits.graph.Block;

public class BasicBlock {
    public int headLine;
    public int tailLine;
    public int blockId;

    public static BasicBlock transform(Block block){
        BasicBlock bb = new BasicBlock();
        Unit head = block.getHead();
        int headLine = Integer.parseInt(head.getTag("LineNumberTag") == null ? "-1" : head.getTag("LineNumberTag").toString());
        Unit tail = block.getTail();
        int tailLine = Integer.parseInt(tail.getTag("LineNumberTag") == null ? "-1" : tail.getTag("LineNumberTag").toString());
        bb.headLine = headLine;
        bb.tailLine = tailLine;
        return bb;
    }
}
