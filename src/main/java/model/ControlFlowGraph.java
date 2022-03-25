package model;

import java.util.List;

public class ControlFlowGraph{
    public String className;
    public String methodName;

    public ControlFlowGraph(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }
    public List<BasicBlock> bbs;
    public int[][] flows;
}