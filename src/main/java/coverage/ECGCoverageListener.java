package coverage;

import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class ECGCoverageListener {
    public static Logger logger = Logger.getLogger(ECGCoverageListener.class);
    public static AtomicReference<ConcurrentMap<String, HashSet<Integer>>> methodRecordMap = new AtomicReference<ConcurrentMap<String, HashSet<Integer>>>(
            new ConcurrentHashMap<String, HashSet<Integer>>());
    public static AtomicReference<ConcurrentMap<String, Integer>> methodCoverageMap = new AtomicReference<ConcurrentMap<String, Integer>>(
            new ConcurrentHashMap<String, Integer>());
    public static AtomicReference<Integer> lines = new AtomicReference<Integer>(
            0);
    public static AtomicReference<Integer> executeLines = new AtomicReference<Integer>(
            0);
    public static AtomicReference<ConcurrentMap<String, List<Pair<Integer,Integer>>>> probes = new AtomicReference<ConcurrentMap<String, List<Pair<Integer,Integer>>>>(
            new ConcurrentHashMap<String, List<Pair<Integer,Integer>>>());
}
