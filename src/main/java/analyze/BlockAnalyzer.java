package analyze;

import storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class BlockAnalyzer {
    public static void analyze(File file) throws IOException {
        String testMethodPrefix="test_method:";
        BufferedReader reader=new BufferedReader(new FileReader(file));
        String line;
        Set<Integer> results=new HashSet<>();
        while((line=reader.readLine())!=null){
            if(line.contains(testMethodPrefix)){
                //test ends
                String testMethod=line.substring(testMethodPrefix.length());
                for(Integer i:results){
                    Storage.exec_blocks.get().putIfAbsent(i,new HashSet<>());
                    Storage.exec_blocks.get().get(i).add(testMethod);
                }
                results.clear();
            }else{
                results.add(Integer.parseInt(line));
            }
        }
    }

}
