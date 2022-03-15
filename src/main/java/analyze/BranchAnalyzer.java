package analyze;

import com.github.javaparser.utils.Pair;
import storage.Storage;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BranchAnalyzer {
    private static final String SEPERATEER="#";
    private static final String DEVIDER="-";
    private Set<String> callsites=new HashSet<>();
    private Map<String,Integer> branches=new HashMap<>();

    private static class LineAnalyzer{
        private boolean skip=false;
        private boolean lastIfFalse=false;
        private String lastIfFalseCallsite;

        public Pair<String,Integer> analyzeLine(String line){
            if(line.contains(DEVIDER)){
                skip=!skip;
                //测试用例最后执行的branch是if的false分支
                if(skip&&lastIfFalse){
                    lastIfFalse=false;
                    return new Pair<>(lastIfFalseCallsite,1);
                }
                return null;
            }
            if(skip){
                return null;
            }
            int index=line.lastIndexOf(SEPERATEER);
            String sub=line.substring(0,index);
            String which=line.substring(index+1);

            //true分支
            if(which.equals("true")){
                lastIfFalse=false;
                return new Pair<>(sub,0);
            }else if(which.equals("false")){
                boolean tmpFalse=lastIfFalse;
                String tmpFalseCallsite=lastIfFalseCallsite;
                lastIfFalse=true;
                lastIfFalseCallsite=sub;
                //上一个是false分支
                if(tmpFalse){
                    return new Pair<>(tmpFalseCallsite,1);
                }
                return null;
            }else{
                //switch
                return new Pair<>(sub,Integer.parseInt(which));
            }
        }
    }

    private static class LineAnalyzer2{
        private boolean skip=false;
        private LineAnalyzer analyzer=new LineAnalyzer();
        private Map<Integer,Map<Integer,Set<String>>> results=new HashMap<>();
        private Map<Integer,Set<Integer>> result=new HashMap<>();

        public boolean analyzeLine(String line){
            //先委托给LineAnalyzer进行分析
            Pair<String,Integer> p=analyzer.analyzeLine(line);
            if(p!=null){
                int index=p.a.lastIndexOf(SEPERATEER);
                int branchId=Integer.parseInt(p.a.substring(index+1));
                int which=p.b;
                result.putIfAbsent(branchId,new HashSet<>());
                result.get(branchId).add(which);
            }

            if(line.contains(DEVIDER)){
                skip=!skip;
                return !skip;
            }

            if(skip){
                String prefix="test_method:";
                if(line.startsWith(prefix)){
                    String testMethod=line.trim().substring(prefix.length());
                    for(int branchId:result.keySet()){
                        results.putIfAbsent(branchId,new HashMap<>());
                        for(int which:result.get(branchId)){
                            results.get(branchId).putIfAbsent(which,new HashSet<>());
                            results.get(branchId).get(which).add(testMethod);
                        }
                    }
                    result.clear();
                }
            }
            return false;
        }
    }

    public void reset(){
        this.branches.clear();
        this.callsites.clear();
    }

    //统计每个方法被调用的分支个数(覆盖率 e.g. 1/2)
    public void analyze(File file) throws IOException {
        final LineAnalyzer lineAnalyzer=new LineAnalyzer();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            Pair<String,Integer> p=lineAnalyzer.analyzeLine(line);
            if(p!=null){
                String method=p.a.substring(0,p.a.lastIndexOf(SEPERATEER));
                int branchId=Integer.parseInt(p.a.substring(p.a.lastIndexOf(SEPERATEER)+1));
                int which=p.b;
                String callsite=method+SEPERATEER+branchId+SEPERATEER+which;
                if(!callsites.contains(callsite)){
                    callsites.add(callsite);
                    branches.putIfAbsent(method,0);
                    branches.put(method,branches.get(method)+1);
                }
            }
        }
        Storage.exec_branches.set(this.branches);
    }

    //统计每个方法每个分支调用情况(被哪些测试用例调用)
    public void analyze2(File file) throws IOException {
        final LineAnalyzer2 lineAnalyzer=new LineAnalyzer2();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            lineAnalyzer.analyzeLine(line);
        }
        Storage.exec_branches2.set(lineAnalyzer.results);
    }
}
