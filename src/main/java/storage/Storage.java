package storage;

import com.github.javaparser.utils.Pair;
import com.kitfox.svg.A;
import coverage.methodAdapter.BranchCoverageMethodAdapter;
import coverage.methodAdapter.CfgMethodAdapter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Storage {
    public static AtomicReference<Map<String,List<String>>> tests=new AtomicReference<>(new HashMap<>());
    public static AtomicReference<Map<String,Set<Integer>>> lines = new AtomicReference<>(new ConcurrentHashMap<>());
    public static AtomicReference<ConcurrentMap<String, List<Pair<Integer,Integer>>>> probes = new AtomicReference<>(
            new ConcurrentHashMap<>());

    public static AtomicReference<Vector<CfgMethodAdapter.ControlFlowGraph>> cfgs=new AtomicReference<>(new Vector<>());

    public static AtomicReference<ConcurrentMap<String, List<BranchCoverageMethodAdapter.BranchStruct>>> branches=new AtomicReference<>(new ConcurrentHashMap<>());

    public static AtomicReference<ConcurrentMap<String,Integer>> paths=new AtomicReference<>(new ConcurrentHashMap<>());

    public static AtomicReference<Map<String, Integer>> exec_lines= new AtomicReference<>(new HashMap<>()); //key: method value: line executed for each method

    public static AtomicReference<Map<String,Map<Integer,Set<String>>>> exec_lines2=new AtomicReference<>(new HashMap<>()); //key: method value: map(key: line number value:set(test method))

    public static AtomicReference<Map<String,Integer>> exec_branches=new AtomicReference<>(new HashMap<>()); //key: method value: branch executed for each method

    public static AtomicReference<Map<Integer,Map<Integer,Set<String>>>> exec_branches2=new AtomicReference<>(new HashMap<>()); //key: branchId value: map(key: which value:set(test method))

    public static AtomicReference<Map<String,Integer>> exec_paths=new AtomicReference<>(new HashMap<>());
}
