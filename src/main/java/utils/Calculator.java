package utils;

import org.apache.log4j.Logger;
import storage.Storage;

public class Calculator {
    private static final Logger logger = Logger.getLogger(Calculator.class);
    public static double calculateStatementCoverage(){
        int lines= Storage.lines.get();
        int executedLines=Storage.executeLines.get();
        logger.info("coverage rate:"+(double)(executedLines)/(double)lines);
        return (double)(executedLines)/(double)lines;
    }
}
