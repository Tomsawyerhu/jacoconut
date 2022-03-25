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
    private Set<Integer> branchIds=new HashSet<>();
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
                    int index=lastIfFalseCallsite.lastIndexOf(SEPERATEER);
                    return new Pair<>(lastIfFalseCallsite.substring(0,index),Integer.parseInt(lastIfFalseCallsite.substring(index+1)));
                }
                return null;
            }
            if(skip){
                return null;
            }
            String[] ss =line.split(SEPERATEER); // class#methodName#methodDesc#branchId Or class#methodName#methodDesc#branchId#trueOrFalse
            int size=ss.length;

            //true分支
            if(ss[size-1].equals("true")){
                lastIfFalse=false;
                return new Pair<>(ss[0]+"#"+ss[1]+"#"+ss[2],Integer.parseInt(ss[3]));
            }else if(ss[size-1].equals("false")){
                boolean tmpFalse=lastIfFalse;
                String tmpFalseCallsite=lastIfFalseCallsite;
                lastIfFalse=true;
                lastIfFalseCallsite=ss[0]+"#"+ss[1]+"#"+ss[2]+"#"+ss[3];
                //上一个是false分支
                if(tmpFalse){
                    int index=tmpFalseCallsite.lastIndexOf(SEPERATEER);
                    return new Pair<>(tmpFalseCallsite.substring(0,index),Integer.parseInt(tmpFalseCallsite.substring(index+1)));
                }
                return null;
            }else{
                //switch
                return new Pair<>(ss[0]+"#"+ss[1]+"#"+ss[2],Integer.parseInt(ss[3]));
            }
        }
    }

    private static class LineAnalyzer2{
        private boolean skip=false;
        private LineAnalyzer analyzer=new LineAnalyzer();
        private Map<Integer,Set<String>> results=new HashMap<>();
        private Set<Integer> result=new HashSet<>();

        public boolean analyzeLine(String line){
            //先委托给LineAnalyzer进行分析
            Pair<String,Integer> p=analyzer.analyzeLine(line);
            if(p!=null){
                int branchId=p.b;
                result.add(branchId);
            }

            if(line.contains(DEVIDER)){
                skip=!skip;
                return !skip;
            }

            if(skip){
                String prefix="test_method:";
                if(line.startsWith(prefix)){
                    String testMethod=line.trim().substring(prefix.length());
                    for(int branchId:result){
                        results.putIfAbsent(branchId,new HashSet<>());
                        results.get(branchId).add(testMethod);
                    }
                    result.clear();
                }
            }
            return false;
        }
    }

    public void reset(){
        this.branches.clear();
        this.branchIds.clear();
    }

    //统计每个方法被调用的分支个数(覆盖率 e.g. 1/2)
    public void analyze(File file) throws IOException {
        final LineAnalyzer lineAnalyzer=new LineAnalyzer();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            Pair<String,Integer> p=lineAnalyzer.analyzeLine(line);
            if(p!=null){
                String method=p.a;
                int branchId=p.b;
                if(!branchIds.contains(branchId)){
                    branchIds.add(branchId);
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
