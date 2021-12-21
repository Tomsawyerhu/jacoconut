package coverage;

import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


public class Tracer {
    private static Logger logger = Logger.getLogger(Tracer.class);

    private static Tracer trace = null;

    private Tracer() {
    }

    public static Tracer getInstance() {
        if (trace == null) {
            trace = new Tracer();
        }
        return trace;
    }

    //fixme
    //记录行数
    public static void recordMethodInfo(String receiver, String mfName,int line) {
        String key = mfName;
        if (receiver.length() > 0) {
            key = "<" + receiver + ">" + key;
        }
        ConcurrentMap<String, HashSet<Integer>> recordMap = ECGCoverageListener.methodRecordMap
                .get();
        if (!recordMap.containsKey(key)) {
            recordMap.put(key, new HashSet<>());
        } else
            recordMap.get(key).add(line);
    }

    public void logMethodInfo(String receiver, String mfName) {
        logger.info("Recording entitiy: " + "<" + receiver + ">" + mfName);

        String key = mfName;
        if (receiver.length() > 0) {
            key = "<" + receiver + ">" + key;
        }
        ConcurrentMap<String, Integer> methodMap = ECGCoverageListener.methodCoverageMap
                .get();
        if (!methodMap.containsKey(key)) {
            methodMap.put(key, 1);
        } else
            methodMap.put(key, methodMap.get(key) + 1);
    }

    /*
     * 记录探针位置
     */
    public static void executeLines(int left,int line,String key) {
        ConcurrentMap<String, List<Pair<Integer, Integer>>> map=ECGCoverageListener.probes.get();
        if(map.containsKey(key)){
            map.get(key).add(new Pair<>(left,line));
        }else{
            List<Pair<Integer, Integer>> list= new ArrayList<>();
            list.add(new Pair<>(left,line));
            map.put(key,list);
        }
    }

    /*
     * 记录执行的行数
     */
    public void executeLines2(int lines) {
        int i=ECGCoverageListener.executeLines.get();
        ECGCoverageListener.lines.compareAndSet(i,i+lines);
    }

}
