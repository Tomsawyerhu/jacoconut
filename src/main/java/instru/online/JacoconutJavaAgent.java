package instru.online;

import config.Properties;
import org.apache.log4j.Logger;

import java.lang.instrument.Instrumentation;

public class JacoconutJavaAgent {
    private static Logger logger=Logger.getLogger(JacoconutJavaAgent.class);
    public static void premain(
            String agentArgs, Instrumentation inst) {
        logger.info("[Agent] In premain method");
        logger.info("system property project.prefix is "+ Properties.PROJECT_PREFIX);
        inst.addTransformer(new JacoconutTransformer());
    }
    public static void agentmain(
            String agentArgs, Instrumentation inst) {
        logger.info("[Agent] In agentmain method");
        logger.info("system property project.prefix is "+ Properties.PROJECT_PREFIX);
        inst.addTransformer(new JacoconutTransformer());
    }
}
