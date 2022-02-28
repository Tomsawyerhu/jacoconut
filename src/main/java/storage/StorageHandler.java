package storage;

import java.util.concurrent.ConcurrentHashMap;

public class StorageHandler {
    public static void resetProbe(){
        Storage.probes.set(new ConcurrentHashMap<>());
    }

    public static void resetLine(){
        Storage.lines.set(0);
    }
}
