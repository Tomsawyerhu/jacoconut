package agent;

import org.apache.log4j.Logger;
import storage.Property;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Jagent {
    private static Logger logger = Logger.getLogger(Jagent.class);
    public static Set<String> excludes;
    public static void premain(String args, Instrumentation inst) {
        logger.info("MyAgent start...");
        excludes= readExcludedTestClasses();
        Property.properties=readConf();
        logger.info(Property.getProperty(Property.PROJECT_PREFIX_KEY)==null?"no prefix":System.getProperty(Property.PROJECT_PREFIX_KEY));
        inst.addTransformer(new ClassTransformer());
    }
    public static void main(String[] args){
        logger.debug("main");
        System.out.println("hello world");
    }
    public static Set<String> readExcludedTestClasses(){
        File file=new File("TestClasses.dat");
        Set<String> res=new HashSet<String>();
        if(!file.exists())
            return res;
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            String line=reader.readLine();
            while(line!=null){
                res.add(line);
                line=reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Properties readConf(){
        Properties properties = new Properties();
        InputStream in = Jagent.class.getClassLoader().getResourceAsStream("config.properties");
        if(in!=null) {
            try {
                properties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
