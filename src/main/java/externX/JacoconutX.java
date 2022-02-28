package externX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JacoconutX {

    private static JacoconutX jacoconutX = null;
    public static final String output="./probe_info.jcn";

    private JacoconutX() {
    }

    public static JacoconutX getInstance() {
        if (jacoconutX == null) {
            jacoconutX = new JacoconutX();
        }
        return jacoconutX;
    }

    public void executeLines(String callsite,int line) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(callsite).append(" ").append(String.valueOf(line)).append("\n");
            fw.flush();
            fw.close();
        }
    }
}
