package storage;

import com.github.javaparser.utils.Pair;
import coverage.methodAdapter.PathCoverageMethodAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Storage {
    public static AtomicReference<Integer> lines = new AtomicReference<Integer>(
            0);
    public static AtomicReference<ConcurrentMap<String, List<Pair<Integer,Integer>>>> probes = new AtomicReference<>(
            new ConcurrentHashMap<>());

    public static AtomicReference<List<PathCoverageMethodAdapter.CfgMethodAdapter.ControlFlowGraph>> cfgs=new AtomicReference<>(new ArrayList<>());



}
