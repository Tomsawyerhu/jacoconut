package analyze;

import com.github.javaparser.utils.Pair;
import storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodAnalyzer {
    private static final String SEPERATEER="#";
    private static final String DEVIDER="-";
    private Map<String,Integer> methods=new HashMap<>();
    private Set<String> callsites=new HashSet<>();

    private static class LineAnalyzer{
        private boolean skip=false;
        Pair<String,String> analyzeLine(String line){
            //lines
            if(line.contains(DEVIDER)){
                skip=!skip;
                return null;
            }
            if(skip){
                return null;
            }
            String[] ss=line.split(SEPERATEER);
            return new Pair<>(ss[0],ss[1]+"#"+ss[2]);
        }
    }

    private static class LineAnalyzer2{
        private boolean skip=false;
        private Map<String,Set<String>> results=new HashMap<>(); //key: method value: map(key: line number value:set(test method))
        private Set<String> result=new HashSet<>(); //key:method value:lines
        boolean analyzeLine(String line){
            //lines
            if(line.contains(DEVIDER)){
                skip=!skip;
                return !skip;
            }
            if(skip){
                String prefix="test_method:";
                if(line.startsWith(prefix)){
                    String testMethod=line.trim().substring(prefix.length());
                    for(String method:result){
                        results.putIfAbsent(method,new HashSet<>());
                        results.get(method).add(testMethod);
                    }
                    result.clear();
                }
                return false;
            }
            String[] ss=line.split(SEPERATEER);
            result.add(ss[0]+"#"+ss[1]+"#"+ss[2]);
            return false;
        }
    }

    public void reset(){
        this.methods.clear();
        this.callsites.clear();
    }

    //统计每个类被调用的方法数(覆盖率，e.g. 10/13)
    public void analyze(File file) throws IOException {
        final LineAnalyzer lineAnalyzer=new LineAnalyzer();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            Pair<String,String> p=lineAnalyzer.analyzeLine(line);
            if(p!=null&&!callsites.contains(p.a+"#"+p.b)){
                String clazz=p.a;
                if(methods.containsKey(clazz)){
                   methods.put(clazz,methods.get(clazz)+1);
                }else{
                    methods.put(clazz,1);
                }
                callsites.add(p.a+"#"+p.b);
            }
        }
        bufferedReader.close();
        Storage.exec_methods.set(this.methods);
    }

    //统计每个方法调用情况(被哪些测试用例调用)
    public void analyze2(File file) throws IOException {
        final LineAnalyzer2 lineAnalyzer=new LineAnalyzer2();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            lineAnalyzer.analyzeLine(line);
        }
        bufferedReader.close();
        Storage.exec_methods2.set(lineAnalyzer.results);
    }

}
