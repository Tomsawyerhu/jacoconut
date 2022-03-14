package analyze;

import com.github.javaparser.utils.Pair;
import storage.Storage;

import java.io.*;
import java.util.*;

public class StatementAnalyzer {
    private static final String SEPERATEER="#";
    private static final String DEVIDER="-";
    private Map<String,Integer> lines=new HashMap<>();
    private Set<String> callsites=new HashSet<>();

    private static class LineAnalyzer{
        private boolean skip=false;
        Pair<String,Integer> analyzeLine(String line){
            //lines
            if(line.contains(DEVIDER)){
                skip=!skip;
                return null;
            }
            if(skip){
                return null;
            }
            int index=line.lastIndexOf(SEPERATEER);
            return new Pair<>(line.substring(0,index),Integer.parseInt(line.substring(index+1)));
        }
    }

    private static class LineAnalyzer2{
        private boolean skip=false;
        private Map<String,Map<Integer,Set<String>>> results=new HashMap<>(); //key: method value: map(key: line number value:set(test method))
        private Map<String,Set<Integer>> result=new HashMap<>(); //key:method value:lines
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
                    for(String method:result.keySet()){
                        Set<Integer> calls=result.get(method);
                        results.putIfAbsent(method,new HashMap<>());
                        for(Integer i:calls){
                            results.get(method).putIfAbsent(i,new HashSet<>());
                            results.get(method).get(i).add(testMethod);
                        }
                    }
                    result.clear();
                }
                return false;
            }
            int index=line.lastIndexOf(SEPERATEER);
            String sub=line.substring(0,index);
            int index2=sub.lastIndexOf(SEPERATEER);
            int l=Integer.parseInt(sub.substring(index2+1));
            String method=sub.substring(0,index2);
            result.putIfAbsent(method,new HashSet<>());
            result.get(method).add(l);
            return false;
        }
    }

    //统计每个方法被调用的行数(覆盖率，e.g. 10/13)
    public void analyze(File file) throws IOException {
        final LineAnalyzer lineAnalyzer=new LineAnalyzer();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            Pair<String,Integer> p=lineAnalyzer.analyzeLine(line);
            if(p!=null&&!callsites.contains(p.a)){
                String method=p.a.substring(0,p.a.lastIndexOf(SEPERATEER));
                if(lines.containsKey(method)){
                    lines.put(method,lines.get(method)+p.b);
                }else{
                    lines.put(method,p.b);
                }
                callsites.add(p.a);
            }
        }
        Storage.exec_lines.set(this.lines);
    }

    //统计每个方法每行调用情况(被哪些测试用例调用)
    public void analyze2(File file) throws IOException {
        final LineAnalyzer2 lineAnalyzer=new LineAnalyzer2();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            lineAnalyzer.analyzeLine(line);
        }
        Storage.exec_lines2.set(lineAnalyzer.results);
    }

    }
