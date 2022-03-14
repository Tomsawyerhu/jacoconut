package storage;

import coverage.methodAdapter.BranchCoverageMethodAdapter;

import java.util.List;

public class StorageHandler {

    public static void setBranch(String method, List<BranchCoverageMethodAdapter.BranchStruct> branches){
        Storage.branches.get().put(method,branches);
    }

    public static void setPath(String method,int paths){
        Storage.paths.get().put(method,paths);
    }


}
