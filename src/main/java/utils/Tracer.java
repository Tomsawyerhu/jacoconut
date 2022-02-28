package utils;

import com.github.javaparser.utils.Pair;
import org.apache.log4j.Logger;
import storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


public class Tracer {
    private static final Logger logger = Logger.getLogger(Tracer.class);

    private static Tracer trace = null;

    private Tracer() {
    }

    public static Tracer getInstance() {
        if (trace == null) {
            trace = new Tracer();
        }
        return trace;
    }

    /*
     * 记录探针位置
     */
    public static void executeLines(String methodName, int start, int line) {
        ConcurrentMap<String, List<Pair<Integer, Integer>>> map= Storage.probes.get();
        if(map.containsKey(methodName)){
            map.get(methodName).add(new Pair<>(start,line));
        }else{
            List<Pair<Integer, Integer>> list= new ArrayList<>();
            list.add(new Pair<>(start,line));
            map.put(methodName,list);
        }
    }

}
