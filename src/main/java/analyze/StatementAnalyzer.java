package analyze;

import com.github.javaparser.utils.Pair;
import storage.Storage;

import java.io.*;
import java.util.*;

public class StatementAnalyzer {
    private static final String SEPERATEER="#";
    private static final String DIVIDER="-";
    private boolean signal;
    private static LineAnalyzer lineAnalyzer=new LineAnalyzer();
    private int lines=0;
    private Set<String> callsites=new HashSet<>();
    private static class LineAnalyzer{
        Pair<String,Integer> analyzeLine1(String line){
            //lines
            int index=line.lastIndexOf(SEPERATEER);
            return new Pair<>(line.substring(0,index),Integer.parseInt(line.substring(index+1)));
        }

        String analyzeLine2(String line){
            //testcase_name
            if(line.startsWith("test_method:")){
                return line.trim().replace("test_method:","");
            }
            return null;
        }
    }

    //目前只是统计每个方法新增的行数
    public void analyze(File file) throws IOException {
        signal=false;
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            if(line.startsWith(DIVIDER)){
                signal=!signal;
                continue;
            }
            if(signal){
                String testcase=lineAnalyzer.analyzeLine2(line);
                if(testcase!=null){
                    Storage.exec_lines.get().add(new Pair<>(testcase,lines));
                    lines=0;
                }
            }else{
                Pair<String,Integer> p=lineAnalyzer.analyzeLine1(line);
                if(!callsites.contains(p.a)){
                    lines+=p.b;
                    callsites.add(p.a);
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            new StatementAnalyzer().analyze(new File("D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\probe_info.jcn"));
            System.out.println(Storage.exec_lines.get().stream().mapToInt(value -> value.b).reduce(Integer::sum).getAsInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
