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
 * className1#methodSig1
 * className2#methodSig2
 * ......
 * classNameN#methodSigN
 * "test_method":testX
 */

public class MethodAnalyzer {
    public static void analyze(File file) throws IOException {
        String testMethodPrefix = "test_method:";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        Set<String> results = new HashSet<>();
        while ((line = reader.readLine()) != null) {
            if (line.contains(testMethodPrefix)) {
                //test ends
                String testMethod = line.substring(testMethodPrefix.length());
                for (String method : results) {
                    Storage.exec_methods2.get().putIfAbsent(method, new HashSet<>());
                    Storage.exec_methods2.get().get(method).add(testMethod);
                }
                results.clear();
            } else {
                results.add(line);
            }
        }
    }
}
