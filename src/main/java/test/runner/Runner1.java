package test.runner;

import api.JacoconutApi;
import api.LCType;

import java.io.IOException;

public class Runner1 {
    public static void main(String[] args) {
        String classFile="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-codec-1_5_RELEASE\\target\\classes\\org\\apache\\commons\\codec\\language\\bm\\Languages$LanguageSet.class";
        try {
            JacoconutApi.lineCoverageProbe(classFile, LCType.NAIVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
