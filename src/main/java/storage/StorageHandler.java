package storage;

public class StorageHandler {

    public static void reset(){
        Storage.tests.get().clear();
        Storage.lines.get().clear();
        Storage.branches.get().clear();
        Storage.exec_lines.get().clear();
        Storage.exec_lines2.get().clear();
        Storage.exec_methods.get().clear();
        Storage.exec_methods2.get().clear();
        Storage.exec_branches.get().clear();
        Storage.exec_branches2.get().clear();
    }


}
