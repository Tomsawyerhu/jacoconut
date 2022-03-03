package test.runner;

import api.JacoconutApi;

import java.io.IOException;

public class Runner1 {
    public static void main(String[] args) {
        String classFile="D:\\BaiduNetdiskDownload\\maven-projects\\maven-projects\\commons-cli-cli-1.4\\target\\classes\\org\\apache\\commons\\cli\\Parser.class";
        try {
            JacoconutApi.lineCoverageProbe(classFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
