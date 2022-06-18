package analyze;

import storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * format:
 * className1#methodSig1#startLine1#lineSpan1
 * className2#methodSig2#startLine2#lineSpan2
 * ......
 * classNameN#methodSigN#startLineN#lineSpanN
 * "test_method":testX
 */

public class StatementAnalyzer {

    public static void analyze(File file) throws IOException {
        String testMethodPrefix="test_method:";
        BufferedReader reader=new BufferedReader(new FileReader(file));
        String line;
        Map<String,Set<Integer>> results=new HashMap<>();
        while((line=reader.readLine())!=null){
            if(line.contains(testMethodPrefix)){
                //test ends
                String testMethod=line.substring(testMethodPrefix.length());
                for(String method:results.keySet()){
                    Storage.exec_lines2.get().putIfAbsent(method,new HashMap<>());
                    for(int l:results.get(method)){
                        Storage.exec_lines2.get().get(method).putIfAbsent(l,new HashSet<>());
                        Storage.exec_lines2.get().get(method).get(l).add(testMethod);
                    }
                }
                results.clear();
            }else{
                String[] ss=line.split("#");
                int span=Integer.parseInt(ss[3]);
                String method=ss[0]+"#"+ss[1];
                results.putIfAbsent(method,new HashSet<>());
                for(int i=0;i<span;i++){
                    results.get(method).add(i+Integer.parseInt(ss[2]));
                }
            }
        }
    }
}
