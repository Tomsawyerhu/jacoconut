package algorithm;

import com.github.javaparser.utils.Pair;
import coverage.methodAdapter.CfgMethodAdapter;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class cfg {
    public static void cfgDrawer(CfgMethodAdapter.ControlFlowGraph cfg, String output) throws IOException {
        String title=String.format("Control FLow Graph For %s#%s",cfg.className,cfg.methodName);
        List<LinkSource> nodes=new ArrayList<>();
        String nodeFormat="start:%d\nsize:%d";
        for(int i=0;i<cfg.bbs.size();i+=1){
            Node n=node(String.format(nodeFormat,cfg.bbs.get(i).startLabel,cfg.bbs.get(i).labelNum));
            boolean isEnd=true;
            for(int j=0;j<cfg.bbs.size();j+=1){
                //忽略自环的情况
                if(cfg.flows[i][j]>0&&i!=j){
                    n=n.link(node(String.format(nodeFormat,cfg.bbs.get(j).startLabel,cfg.bbs.get(j).labelNum)));
                    if(isEnd){
                        isEnd= false;
                    }
                }
            }
            if(!isEnd){
                nodes.add(n);
            }
        }
        Graph g = graph(title).directed()
                .linkAttr().with("class", "link-class")
                .with(
                        nodes
                );
        Graphviz.fromGraph(g).height(700).width(500).render(Format.PNG).toFile(new File(output));
    }

    public static class CfgPathOptions{
        public int limit_path_length=20;
        public int limit_loop_times=10;
    }

    //todo how to handle loop?
    public static int cfgPaths(CfgMethodAdapter.ControlFlowGraph cfg, CfgPathOptions options){
        int[][] flows=cfg.flows;
        if(flows.length==0){return 0;}
        List<Set<CfgPath>> IN_LIST=new ArrayList<>();
        List<Set<CfgPath>> OUT_LIST=new ArrayList<>();

        //初始化
        for(int i=0;i<flows.length;i+=1){
            IN_LIST.add(new HashSet<>());
            OUT_LIST.add(new HashSet<>());
        }

        List<Pair<CfgPath,Integer>> WORKSET = new ArrayList<>();
        List<Integer> headBlockIndexes=new ArrayList<>();
        for(int i=0;i<flows.length;i+=1){
            boolean isHead=true;
            for (int[] flow : flows) {
                if (flow[i] != 0) {
                    isHead = false;
                    break;
                }
            }
            if(isHead){
                headBlockIndexes.add(i);
            }
        }

        for(int headBlockIndex:headBlockIndexes){
            WORKSET.add(new Pair<>(CfgPath.rootPath(), headBlockIndex));
        }
        if(!headBlockIndexes.contains(0)){WORKSET.add(new Pair<>(CfgPath.rootPath(), 0));}

        Map<Pair<Integer,Integer>,Integer> loopTimes=new HashMap<>();

        while(!WORKSET.isEmpty()){
            Pair<CfgPath,Integer> target=WORKSET.remove(0);
            //将工作集加入IN
            IN_LIST.get(target.b).add(target.a);
            //更新OUT
            CfgPath newPath=target.a.extend(cfg.bbs.get(target.b).blockId);
            //propagate
            if((newPath.blockIds.size()-1)<=options.limit_path_length&&!OUT_LIST.get(target.b).contains(newPath)){
                OUT_LIST.get(target.b).add(newPath);
                for(int i=0;i<flows.length;i+=1){
                    if(flows[target.b][i]>0&&target.b!=i){
                        if(target.b>i) {
                            //loop
                            Pair<Integer, Integer> loopSite = new Pair<>(target.b, i);
                            if (loopTimes.containsKey(loopSite)) {
                                loopTimes.put(loopSite, loopTimes.get(loopSite) + 1);
                            } else {
                                loopTimes.put(loopSite, 1);
                            }
                            int loops=loopTimes.get(loopSite);
                            if(loops<=options.limit_loop_times&&!IN_LIST.get(i).contains(newPath)){
                                WORKSET.add(new Pair<>((CfgPath) newPath.clone(),i));
                            }
                        }else{
                            if(!IN_LIST.get(i).contains(newPath))WORKSET.add(new Pair<>((CfgPath) newPath.clone(),i));
                        }
                    }
                }
            }
        }

        //找出所有终点
        int paths=0;
        for(int i=0;i<flows.length;i+=1){
            boolean isEnd=true;
            for(int j=0;j<flows.length;j+=1){
                if(flows[i][j]>0&&i!=j){
                    isEnd=false;
                    break;
                }
            }
            if(isEnd){
                paths+=OUT_LIST.get(i).size();
            }
        }
        return paths;
    }

    private static class CfgPath{
        List<Integer> blockIds;

        CfgPath(int size){
            this.blockIds=new ArrayList<>(Arrays.asList(new Integer[size]));
        }

        CfgPath(){
            this.blockIds=new ArrayList<>();
        }

        public CfgPath extend(int blockId) {
            CfgPath newPath=new CfgPath(this.blockIds.size());
            Collections.copy(newPath.blockIds,this.blockIds);
            newPath.blockIds.add(blockId);
            return newPath;
        }

        public static CfgPath rootPath(){
            CfgPath rootPath=new CfgPath();
            rootPath.blockIds.add(-1);
            return rootPath;
        }

        @Override
        protected Object clone() {
            CfgPath another=new CfgPath(this.blockIds.size());
            Collections.copy(another.blockIds,this.blockIds);
            return another;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof CfgPath))return false;
            CfgPath another=(CfgPath) obj;
            if(another.blockIds.size()!=this.blockIds.size()){
                return false;
            }
            for(int i=0;i<this.blockIds.size();i+=1){
                if((int)this.blockIds.get(i)!= another.blockIds.get(i)){
                    return false;
                }
            }
            return true;

        }

        @Override
        public int hashCode() {
            return 0;
        }

        public int length(){
            return blockIds.size();
        }

    }
}
