package algorithm;

import coverage.methodAdapter.PathCoverageMethodAdapter;
import guru.nidi.graphviz.attribute.Font;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.*;

public class cfg {
    public static void cfgDrawer(PathCoverageMethodAdapter.CfgMethodAdapter.ControlFlowGraph cfg,String output) throws IOException {
        String title=String.format("Control FLow Graph For %s#%s",cfg.className,cfg.methodName);
        List<LinkSource> nodes=new ArrayList<>();
        String nodeFormat="start:%d\nsize:%d";
        for(int i=0;i<cfg.bbs.size();i+=1){
            Node n=node(String.format(nodeFormat,cfg.bbs.get(i).startline,cfg.bbs.get(i).lines));
            boolean isEnd=true;
            for(int j=0;j<cfg.bbs.size();j+=1){
                //忽略自环的情况
                if(cfg.flows[i][j]>0&&i!=j){
                    n=n.link(node(String.format(nodeFormat,cfg.bbs.get(j).startline,cfg.bbs.get(j).lines)));
                    if(isEnd){
                        isEnd= false;
                    }
                }
            }
            if(!isEnd){
                nodes.add(n);
            }
        }
        Graph g = graph("example1").directed()
                .graphAttr().with(Rank.dir(LEFT_TO_RIGHT))
                .nodeAttr().with(Font.name("arial"))
                .linkAttr().with("class", "link-class")
                .with(
                        nodes
                );
        Graphviz.fromGraph(g).height(100).render(Format.PNG).toFile(new File(output));
    }
}
