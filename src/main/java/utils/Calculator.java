package utils;

import org.apache.log4j.Logger;
import storage.Storage;

public class Calculator {
    private static Logger logger = Logger.getLogger(Calculator.class);
    public static void calculateStatementCoverage(){
        int lines= Storage.lines.get();
        int executedLines=Storage.executeLines.get();

        logger.info("coverage rate:"+(double)(executedLines)/(double)lines);
    }


}
