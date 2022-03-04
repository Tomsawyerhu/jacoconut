package test.runner;

import algorithm.cfg;
import coverage.classAdapter.CoverageClassAdapter;
import coverage.methodAdapter.PathCoverageMethodAdapter;
import coverage.methodAdapter.SCType;
import org.objectweb.asm.ClassReader;
import storage.Storage;

import java.io.FileInputStream;
import java.io.IOException;

public class Runner3 {
    public static void main(String[] args) {
        String classFile="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\target\\classes\\org\\apache\\commons\\cli\\DefaultParser.class";
        FileInputStream inputStream= null;
        try {
            inputStream = new FileInputStream(classFile);
            ClassReader cr=new ClassReader(inputStream);
            CoverageClassAdapter coverageClassAdapter=new CoverageClassAdapter(null,SCType.BASIC_BLOCK_RECORD);
            cr.accept(coverageClassAdapter,ClassReader.SKIP_FRAMES);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream = new FileInputStream(classFile);
            ClassReader cr=new ClassReader(inputStream);
            CoverageClassAdapter coverageClassAdapter=new CoverageClassAdapter(null,SCType.BASIC_BLOCK_CFG);
            cr.accept(coverageClassAdapter,ClassReader.SKIP_FRAMES);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        int i=1;
        for(PathCoverageMethodAdapter.CfgMethodAdapter.ControlFlowGraph c:Storage.cfgs.get()){
            String fileName="pic"+i;
            try {
                cfg.cfgDrawer(c,"D:/"+fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            i+=1;
        }

    }
}
