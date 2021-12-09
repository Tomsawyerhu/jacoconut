package my;

import coverage.ECGCoverageListener;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;

public class Calculator {
    private static Logger logger = Logger.getLogger(Calculator.class);
    public void calculateStatementCoverage(){
        ConcurrentMap<String, HashSet<Integer>> recordMap= ECGCoverageListener.methodRecordMap.get();
        ConcurrentMap<String, Integer> methodMap= ECGCoverageListener.methodCoverageMap.get();
        int allStatements=0;
        int uncalledStatements=0;
        for(String method:recordMap.keySet()){
            allStatements+= recordMap.get(method).size();

            for(int line:recordMap.get(method)){
                if(!methodMap.containsKey(method+":"+line)){
                    uncalledStatements+=1;
                }
            }
        }
        logger.info("coverage rate:"+(double)(allStatements-uncalledStatements)/(double)allStatements);
    }


}
