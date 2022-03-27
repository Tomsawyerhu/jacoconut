package externX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JacoconutX {

    private static JacoconutX jacoconutX = null;
    public static final String output="probe_info.jcn";

    private static Set<Integer> branches=new HashSet<>();
    private static Set<Integer> ifFalse=new HashSet<>();

    private static Set<String> lines=new HashSet<>();
    private static Set<String> methods=new HashSet<>();

    private JacoconutX() {
    }

    //path coverage

    public static JacoconutX getInstance() {
        if (jacoconutX == null) {
            jacoconutX = new JacoconutX();
        }
        return jacoconutX;
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

    //line coverage

    public void executeLines(String callsite,int line) throws IOException {
        String ss=callsite+"#"+line;
        if(lines.contains(ss))return;

        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(ss).append("\n");
            fw.flush();
            fw.close();
            lines.add(ss);
        }
    }

    //method coverage

    public void executeMethod(String method) throws IOException {
        if(methods.contains(method))return;
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
            methods.add(method);
        }
    }

    //branch coverage

    //goto switch
    public void executeBranch(int branchId) throws IOException {
        if(branches.contains(branchId)) return;
        File file=new File(output);
        boolean flag;
        if(!file.exists()){
            flag=file.createNewFile();
        }else{
            flag=file.isFile()&& file.canWrite();
        }
        if(flag){
            FileWriter fw=new FileWriter(file,true);
            fw.append(String.valueOf(branchId)).append("\n");
            fw.flush();
            fw.close();
            branches.add(branchId);
        }
    }



    //if else
    public void executeBranch(int branchId,boolean isTrue) throws IOException {
        //true分支
        if(isTrue){
            if(!branches.contains(branchId)){
                File file=new File(output);
                boolean flag;
                if(!file.exists()){
                    flag=file.createNewFile();
                }else{
                    flag=file.isFile()&& file.canWrite();
                }
                if(flag){
                    FileWriter fw=new FileWriter(file,true);
                    fw.append(String.valueOf(branchId)).append("\n");
                    fw.flush();
                    fw.close();
                    //remove false
                    ifFalse.remove(branchId+1);
                    branches.add(branchId);
                }
            }
        }else {
            if(ifFalse.contains(branchId)&&!branches.contains(branchId)){
                File file=new File(output);
                boolean flag;
                if(!file.exists()){
                    flag=file.createNewFile();
                }else{
                    flag=file.isFile()&& file.canWrite();
                }
                if(flag){
                    FileWriter fw=new FileWriter(file,true);
                    fw.append(String.valueOf(branchId)).append("\n");
                    fw.flush();
                    fw.close();
                    branches.add(branchId);
                }
            }

            ifFalse.add(branchId);
        }
    }

    //for each test

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
            for(int i:ifFalse){
                if(!branches.contains(i))fw.append(String.valueOf(i)).append("\n");
            }
            fw.append(String.format("--------------------\ntest_method:%s\n--------------------\n",method));
            fw.flush();
            fw.close();
        }
        ifFalse.clear();
        branches.clear();
        lines.clear();
        methods.clear();
    }
}
