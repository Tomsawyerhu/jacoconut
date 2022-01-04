package test.runner;
import api.LCType;
import test.TestA;
import utils.Calculator;

import java.io.IOException;

public class TestRunnerA {
    public static void main(String[] args) {
        try {
            api.JacoconutApi.lineCoverageProbe("C:\\Users\\tom\\Desktop\\jacoconut\\target\\classes\\test\\TestA.class", LCType.BASIC_BLOCK_RECORD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new TestA().func1(1);
        Calculator.calculateStatementCoverage();
    }
}
