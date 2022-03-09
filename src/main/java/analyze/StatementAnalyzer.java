package analyze;

import com.github.javaparser.utils.Pair;
import storage.Storage;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StatementAnalyzer {
    private static final String SEPERATEER="#";
    private static final LineAnalyzer lineAnalyzer=new LineAnalyzer();
    private ConcurrentMap<String,Integer> lines=new ConcurrentHashMap<>();
    private Set<String> callsites=new HashSet<>();
    private static class LineAnalyzer{
        Pair<String,Integer> analyzeLine(String line){
            //lines
            int index=line.lastIndexOf(SEPERATEER);
            return new Pair<>(line.substring(0,index),Integer.parseInt(line.substring(index+1)));
        }
    }

    //目前只是统计每个方法被调用的行数
    public void analyze(File file) throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            Pair<String,Integer> p=lineAnalyzer.analyzeLine(line);
            if(!callsites.contains(p.a)){
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
}
