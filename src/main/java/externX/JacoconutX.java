package externX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JacoconutX {

    private static JacoconutX jacoconutX = null;
    public static final String output="./probe_info.jcn";
    private Set<String> tokens=new HashSet<>();

    private JacoconutX() {
    }

    public static JacoconutX getInstance() {
        if (jacoconutX == null) {
            jacoconutX = new JacoconutX();
        }
        return jacoconutX;
    }

    public void executeLines(String callsite,int line) throws IOException {
        String token=callsite+"#"+line;
        if(tokens.contains(token))return;

        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(token).append("\n");
            fw.flush();
            fw.close();
            tokens.add(token);
        }
    }
}
