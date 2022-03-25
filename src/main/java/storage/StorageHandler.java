package storage;


import model.BranchStruct;

import java.util.List;

public class StorageHandler {

    public static void setBranch(String method, List<BranchStruct> branches){
        Storage.branches.get().put(method,branches);
    }

    public static void setPath(String method,int paths){
        Storage.paths.get().put(method,paths);
    }


}
