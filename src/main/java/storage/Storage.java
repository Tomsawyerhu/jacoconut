package storage;

import com.github.javaparser.utils.Pair;
import coverage.methodAdapter.BranchCoverageMethodAdapter;
import coverage.methodAdapter.PathCoverageMethodAdapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Storage {
    public static AtomicReference<Map<String,Integer>> lines = new AtomicReference<>(new HashMap<>());
    public static AtomicReference<ConcurrentMap<String, List<Pair<Integer,Integer>>>> probes = new AtomicReference<>(
            new ConcurrentHashMap<>());

    public static AtomicReference<Vector<PathCoverageMethodAdapter.CfgMethodAdapter.ControlFlowGraph>> cfgs=new AtomicReference<>(new Vector<>());

    public static AtomicReference<ConcurrentMap<String, List<BranchCoverageMethodAdapter.BranchStruct>>> branches=new AtomicReference<>(new ConcurrentHashMap<>());

    public static AtomicReference<ConcurrentMap<String,Integer>> paths=new AtomicReference<>(new ConcurrentHashMap<>());



}
