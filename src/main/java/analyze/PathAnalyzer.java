package analyze;

import storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class PathAnalyzer {
    private static final String SEPARATOR ="#";
    private static final String DIVIDER ="-";

    private static class Path{
        private final String PathSeparator ="|";
        StringBuffer value;
        Path(){
            this.value=new StringBuffer();
        }

        void append(String s){
            if(this.value.toString().length()>0)this.value.append(PathSeparator);
            this.value.append(s);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Path){
                return ((Path) obj).value.toString().equals(this.value.toString());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        public boolean isLoop(){
            if(!this.value.toString().contains(PathSeparator)) {return false;}
            List<String> s= Arrays.asList(this.value.toString().split(PathSeparator));
            Set<String> ss=new HashSet<>(s);
            for(String sss:ss){
                if(s.stream().filter(s1 -> s1.equals(sss)).count()>=3){return true;};
            }
            return false;
        }

    }
    private static class LineAnalyzer{
        private Stack<Path> paths=new Stack<>();
        private Map<String,Set<Path>> existPaths=new HashMap<>();
        private boolean skip=false;
        void analyzeLine(String line){
            //lines
            if(line.contains(DIVIDER)){
                skip=!skip;
                return;
            }
            if(skip){
                return;
            }
            if(line.endsWith("start")){
                paths.push(new Path());
            }else if(line.endsWith("end")){
                Path p=paths.pop();
                String method=line.substring(0,line.lastIndexOf(SEPARATOR));
                existPaths.putIfAbsent(method,new HashSet<>());
                //如果是循环则不加入
                if(!p.isLoop()){
                    existPaths.get(method).add(p);
                }

            }else{
                paths.peek().append(line);
            }
        }
    }

    //统计每个方法覆盖的路径数量(覆盖率 e.g. 1/2)
    public void analyze(File file) throws IOException {
        final LineAnalyzer lineAnalyzer=new LineAnalyzer();
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
        String line;
        while((line=bufferedReader.readLine())!=null){
            lineAnalyzer.analyzeLine(line);
        }
        Map<String,Integer> methodPaths=new HashMap<>();
        for(String key:lineAnalyzer.existPaths.keySet()){
            methodPaths.put(key,lineAnalyzer.existPaths.get(key).size());
        }
        Storage.exec_paths.set(methodPaths);
    }
}
