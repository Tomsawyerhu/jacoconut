package storage;

import com.github.javaparser.utils.Pair;
import core.branch.BranchCoverageMethodAdapter;
import model.BasicBlock;
import model.Edge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class Storage {
    public static AtomicReference<Map<String,List<String>>> tests=new AtomicReference<>(new HashMap<>());

    public static AtomicReference<Map<String,Set<Integer>>> lines = new AtomicReference<>(new ConcurrentHashMap<>());

    public static AtomicReference<ConcurrentMap<String, List<BranchCoverageMethodAdapter.BranchStruct>>> branches=new AtomicReference<>(new ConcurrentHashMap<>());

    public static AtomicReference<Map<String, Integer>> exec_lines= new AtomicReference<>(new HashMap<>()); //key: method value: line executed for each method

    public static AtomicReference<Map<String,Map<Integer,Set<String>>>> exec_lines2=new AtomicReference<>(new HashMap<>()); //key: method value: map(key: line number value:set(test method))

    public static AtomicReference<Map<String,Integer>> exec_branches=new AtomicReference<>(new HashMap<>()); //key: method value: branch executed for each method

    public static AtomicReference<Map<Integer,Set<String>>> exec_branches2=new AtomicReference<>(new HashMap<>()); //key: branchId value:set(test method)

    public static AtomicReference<Map<String,Set<String>>> methods=new AtomicReference<>(new HashMap<>());

    public static AtomicReference<Map<String,Integer>> exec_methods=new AtomicReference<>(new HashMap<>()); //key: class value: method executed for each class

    public static AtomicReference<Map<String,Set<String>>> exec_methods2=new AtomicReference<>(new HashMap<>()); //key: method value: set(test method)

    public static AtomicReference<Map<String, Set<BasicBlock>>> blocks=new AtomicReference<>(new HashMap<>());

    public static AtomicReference<Map<Integer, Set<String>>> exec_blocks =new AtomicReference<>(new HashMap<>()); //key: blockId value: set(test method)

    public static AtomicReference<Map<String, Set<Edge>>> edges=new AtomicReference<>(new HashMap<>());

    public static AtomicReference<Map<Pair<Integer,Integer>, Set<String>>> possible_exec_edges =new AtomicReference<>(new HashMap<>()); //key: Pair(startBlockId,endBlockId) value: set(test method)
}
