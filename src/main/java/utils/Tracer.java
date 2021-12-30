package utils;

import storage.Storage;
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

    /*
     * 记录探针位置
     */
    public static void executeLines(int left,int line,String key) {
        ConcurrentMap<String, List<Pair<Integer, Integer>>> map= Storage.probes.get();
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
        int i= Storage.executeLines.get();
        Storage.executeLines.compareAndSet(i,i+lines);
    }

}