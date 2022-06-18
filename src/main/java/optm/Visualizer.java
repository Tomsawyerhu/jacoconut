package optm;
import model.BasicBlock;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.view.Viewer;

import java.io.*;

public class Visualizer {
    public static Visualizer instance;

    private Graph g;
    private Viewer viewer;

    private final static String[] colors = new String[]{"black","white","red", "blue", "green"};

    public static Visualizer v() {
        if(instance == null)
            instance = new Visualizer();
        return instance;
    }

    private Visualizer(){
        if(g!=null){g.clear();}
        if(viewer!=null){viewer.close();}
        g= new SingleGraph("block graph");

        g.addAttribute("ui.quality");
        g.addAttribute("ui.antialias");
        String GCSS = "";
        GCSS += "node {\n" +
                "}\n";


        for(String color : colors) {
            for(String color2 : colors) {
                GCSS += "node." + color + "_"+color2+ " {\n" +
                        "\tsize-mode: fit;\n" +
                        "\tshape: rounded-box;\n" +
                        "\tstroke-mode: plain;\n" +
                        "\tpadding: 5px, 5px;\n" +
                        "\tfill-color: " + color + ";\n" +
                        "\ttext-size: 16;\n" +
                        "\ttext-color: " + color2 + ";\n" +
                        "}\n";
            }
        }
        for(String color:colors){
            GCSS += "edge." + color +" {\n" +
                    "\tfill-color: " + color + ";\n" +
                    "}\n";
        }
        g.setAttribute("ui.stylesheet", GCSS);
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    }

    public void addGraph(BlockGraph graph) {
        int index = 0;
        for (BasicBlock bb : graph.blocks) {
            Node blockNode = g.addNode(String.valueOf(index));
            if (graph.isIn(index)) {
                blockNode.addAttribute("ui.class", "white_green");
            } else if (graph.isExit(index)) {
                blockNode.addAttribute("ui.class", "white_red");
            } else {
                blockNode.addAttribute("ui.class", "black_white");
            }

            blockNode.addAttribute("ui.label", getCode(bb, graph.linkingFile));
            index += 1;
        }
        for (int i :graph.flowsParentSons.keySet()) {
            for (int j:graph.flowsParentSons.get(i)) {
                g.addEdge(i + "->" + j, String.valueOf(i), String.valueOf(j), true);
            }
        }
    }

    public void addGraph(DfsBlockGraph graph){
        for(int i = 0; i<graph.marksByVertex.length; i++){
            Node blockNode=g.addNode(String.valueOf(i));
            blockNode.addAttribute("ui.class","black_white");
            blockNode.addAttribute("ui.label",graph.marksByVertex[i]);
        }
        for(int i:graph.flowsParentSons.keySet()){
            for(int j:graph.flowsParentSons.get(i)){
                g.addEdge(i+"->"+j,String.valueOf(i),String.valueOf(j),true).addAttribute("ui.class","black");
            }
        }
    }

    public void addGraph(DominatorTreeGraph graph){
        for(int i=0;i<graph.marks.length;i++){
            if(graph.marks[i]>0){
                Node blockNode=g.addNode(String.valueOf(graph.marks[i]));
                blockNode.addAttribute("ui.class","black_white");
                blockNode.addAttribute("ui.label",graph.marks[i]);
            }
        }
        for(int i:graph.dominatorTree.keySet()){
            for(int j:graph.dominatorTree.get(i)){
                g.addEdge(i+1+"->"+(j+1),String.valueOf(i+1),String.valueOf(j+1),true).addAttribute("ui.class","black");
            }
        }

    }



    private static String getCode(BasicBlock block,String file){
        int start=block.headLine-1;
        int end=block.tailLine-1;
        if(start<0||start>end){
            return "code cannot be read";
        }

        int limit=5;
        File f=new File(file);

        try {
            BufferedReader reader=new BufferedReader(new FileReader(f));
            StringBuilder s= new StringBuilder();
            int count=0;
            while(count<start){
                reader.readLine();
                count++;
            }
            for(int i=start;i<=end;i+=1){
                if(i-start<limit) s.append(reader.readLine().trim()).append("\n");
                else{s.append(String.format("%d lines left ...\n",end-i+1));break;}
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "code cannot be read";
        }
    }

    public void draw(){
        if(viewer != null)
            viewer.close();
        viewer = g.display();
        Layout layout = new LinLog();
        viewer.enableAutoLayout(layout);
    }


}
