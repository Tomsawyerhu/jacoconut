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

    public static void reset(){
        Storage.tests.get().clear();
        Storage.lines.get().clear();
        Storage.probes.get().clear();
        Storage.cfgs.get().clear();
        Storage.branches.get().clear();
        Storage.paths.get().clear();
        Storage.exec_lines.get().clear();
        Storage.exec_lines2.get().clear();
        Storage.exec_methods.get().clear();
        Storage.exec_methods2.get().clear();
        Storage.exec_paths.get().clear();
        Storage.exec_branches.get().clear();
        Storage.exec_branches2.get().clear();
    }


}
