package analyze;

import com.github.javaparser.utils.Pair;
import storage.Storage;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * format:
 * blockId1
 * blockId2
 * ......
 * blockIdN
 * "test_method":testX
 */

public class EdgeAnalyzer {
    public static void analyze(File file) throws IOException {
        String testMethodPrefix="test_method:";
        BufferedReader reader=new BufferedReader(new FileReader(file));
        String line;
        Integer startBlockId,endBlockId=null;
        Set<Pair<Integer,Integer>> results=new HashSet<>();
        while((line=reader.readLine())!=null){
            if(line.contains(testMethodPrefix)){
                //test ends
                String testMethod=line.substring(testMethodPrefix.length());
                for(Pair<Integer,Integer> p:results){
                    Storage.possible_exec_edges.get().putIfAbsent(p,new HashSet<>());
                    Storage.possible_exec_edges.get().get(p).add(testMethod);
                }
                results.clear();
                endBlockId=null;
            }else{
                startBlockId=endBlockId;
                endBlockId=Integer.parseInt(line);
                if(startBlockId != null){
                    results.add(new Pair<>(startBlockId,endBlockId));
                }
            }
        }
    }
}
