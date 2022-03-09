package externX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JacoconutX {

    private static JacoconutX jacoconutX = null;
    public static final String output="probe_info.jcn";
    private static Set<String> tokens=new HashSet<>();

    private JacoconutX() {
    }

    public static JacoconutX getInstance() {
        if (jacoconutX == null) {
            jacoconutX = new JacoconutX();
        }
        return jacoconutX;
    }

    public void executeLines(String callsite,int line) throws IOException {
        String token="StatementCoverageToken:"+callsite+"#"+line;
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
            fw.append(callsite).append("#").append(String.valueOf(line)).append("\n");
            fw.flush();
            fw.close();
            tokens.add(token);
        }
    }

    public void methodStart(String method) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(method).append("#").append("start").append("\n");
            fw.flush();
            fw.close();
        }
    }

    public void methodEnd(String method) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(method).append("#").append("end").append("\n");
            fw.flush();
            fw.close();
        }
    }

    //goto switch
    public void executeBranch(String callsite,int branchId,int which) throws IOException {
        String token="BranchCoverageToken:"+callsite+"#"+branchId+"#"+which;
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
            fw.append(callsite).append("#").append(String.valueOf(branchId)).append("#").append(String.valueOf(which)).append("\n");
            fw.flush();
            fw.close();
            tokens.add(token);
        }
    }

    //if else
    public void executeBranch(String callsite,int branchId,boolean which) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(callsite).append("#").append(String.valueOf(branchId)).append("#").append(which?"true":"false").append("\n");
            fw.flush();
            fw.close();
        }
    }
}
