package test.runner;
import test.TestB;
import utils.Calculator;

import java.io.IOException;


public class TestRunnerB {
    public static void main(String[] args) {
        try {
            api.JacoconutApi.lineCoverageProbe("test/TestB","C:\\Users\\tom\\Desktop\\jacoconut\\target\\classes\\test\\TestB.class");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new TestB().testCoverage();
        Calculator.calculateStatementCoverage();
    }
}
