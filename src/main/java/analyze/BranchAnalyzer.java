package analyze;

import com.github.javaparser.utils.Pair;
import coverage.methodAdapter.BranchCoverageMethodAdapter;
import org.apache.log4j.Logger;
import storage.Storage;

import java.io.*;
import java.util.*;

public class BranchAnalyzer {
    private static final String DEVIDER="-";
    private Set<Integer> branchIds=new HashSet<>();
    private Map<String,Integer> branches=new HashMap<>();

    private static class LineAnalyzer{
        private boolean skip=false;

        public Integer analyzeLine(String line){
            if(line.contains(DEVIDER)){
                skip=!skip;
                return null;
            }
            if(skip){
                return null;
            }
            return Integer.parseInt(line.trim());
        }
    }

    private static class LineAnalyzer2{
        private boolean skip=false;
        private LineAnalyzer analyzer=new LineAnalyzer();
        private Map<Integer,Set<String>> results=new HashMap<>();
        private Set<Integer> result=new HashSet<>();

        public boolean analyzeLine(String line){
            //先委托给LineAnalyzer进行分析
            Integer i=analyzer.analyzeLine(line);
            if(i!=null){
                result.add(i);
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
        Set<Integer> branchIds=new HashSet<>();
        String line;
        while((line=bufferedReader.readLine())!=null){
            Integer i=lineAnalyzer.analyzeLine(line);
            if(i!=null){branchIds.add(i);}
        }
        bufferedReader.close();
        for(String method:Storage.branches.get().keySet()){
            List<BranchCoverageMethodAdapter.BranchStruct> branchList=Storage.branches.get().get(method);
            for(BranchCoverageMethodAdapter.BranchStruct branchStruct:branchList){
                int id=branchStruct.id();
                if(branchIds.contains(id)){
                    if(!this.branches.containsKey(method)){
                        this.branches.put(method,1);
                    }else{
                        this.branches.put(method,this.branches.get(method)+1);
                    }
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
        bufferedReader.close();
        Storage.exec_branches2.set(lineAnalyzer.results);
    }
}
