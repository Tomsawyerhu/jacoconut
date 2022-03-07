package test.runner;

import algorithm.cfg;
import coverage.classAdapter.CoverageClassAdapter;
import coverage.methodAdapter.CfgMethodAdapter;
import coverage.methodAdapter.SCType;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import storage.Storage;

import java.io.FileInputStream;
import java.io.IOException;

public class Runner3 {
    private static Logger logger= Logger.getLogger(Runner3.class);
    public static void main(String[] args) {
        String classFile="C:\\Users\\tom\\Desktop\\MathObj.class";
        FileInputStream inputStream= null;
        try {
            inputStream = new FileInputStream(classFile);
            ClassReader cr=new ClassReader(inputStream);
            CoverageClassAdapter coverageClassAdapter=new CoverageClassAdapter(null,SCType.CFG);
            cr.accept(coverageClassAdapter,ClassReader.SKIP_FRAMES);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cfg.CfgPathOptions options=new cfg.CfgPathOptions();
        options.limit_path_length=100;
        int i=1;
        for(CfgMethodAdapter.ControlFlowGraph c:Storage.cfgs.get()){
            try {
                cfg.cfgDrawer(c,"pic"+i+".png");
                logger.info("pic"+i+".png:"+cfg.cfgPaths(c,options));
            } catch (IOException e) {
                e.printStackTrace();
            }

            i+=1;
        }

    }
}
