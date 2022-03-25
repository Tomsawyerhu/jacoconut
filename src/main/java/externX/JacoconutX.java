package externX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JacoconutX {

    private static JacoconutX jacoconutX = null;
    public static final String output="probe_info.jcn";

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
            fw.append(callsite).append("#").append(String.valueOf(line)).append("\n");
            fw.flush();
            fw.close();
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
    public void executeBranch(String callsite,int branchId) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(callsite).append("#").append(String.valueOf(branchId)).append("\n");
            fw.flush();
            fw.close();
        }
    }

    public void executeLabel(String callsite,int labelId) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(callsite).append("#").append(String.valueOf(labelId)).append("\n");
            fw.flush();
            fw.close();
        }
    }

    //if else
    public void executeBranch(String callsite,int branchId,boolean isTrue) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(callsite).append("#").append(String.valueOf(branchId)).append("#").append(isTrue?"true":"false").append("\n");
            fw.flush();
            fw.close();
        }
    }


    //test end
    public void testEnd(String method) throws IOException {
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(String.format("--------------------\ntest_method:%s\n--------------------\n",method));
            fw.flush();
            fw.close();
        }
    }
}
