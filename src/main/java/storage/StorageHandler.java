package storage;

import coverage.methodAdapter.BranchCoverageMethodAdapter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StorageHandler {
    public static void resetProbe(){
        Storage.probes.set(new ConcurrentHashMap<>());
    }

    public static void resetLine(){
        Storage.lines.set(new ConcurrentHashMap<>());
    }

    public static void resetBranch(){
        Storage.branches.set(new ConcurrentHashMap<>());
    }

    public void resetPath(){
        Storage.paths.set(new ConcurrentHashMap<>());
    }

    public static void setLine(String method,int line){
        Storage.lines.get().put(method,line);
    }

    public static void setBranch(String method, List<BranchCoverageMethodAdapter.BranchStruct> branches){
        Storage.branches.get().put(method,branches);
    }

    public static void setPath(String method,int paths){
        Storage.paths.get().put(method,paths);
    }


}
