package instru.online;

import config.Properties;
import instru.transformer.ClassTransformer;
import org.apache.log4j.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;


public class JacoconutTransformer implements ClassFileTransformer {
    private static Logger logger=Logger.getLogger(JacoconutTransformer.class);
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        logger.info(String.format("transform class %s, classloader is %s, systemclassloader is %s",className,loader,ClassLoader.getSystemClassLoader()));
        if (className == null) {
            return classfileBuffer;
        }
        if (loader != ClassLoader.getSystemClassLoader()) {
            return classfileBuffer;
        }

        if (!className.startsWith(Properties.PROJECT_PREFIX.replace('.',
                '/'))) {
            return classfileBuffer;
        }

        if(className.endsWith("Test")||className.endsWith("TestCase")){
            logger.info("find test class "+className);
            return ClassTransformer.transform4(classfileBuffer);
        }

        switch (Properties.COV_TYPE) {
            case "line":
                return ClassTransformer.transform1(classfileBuffer);
            case "branch":
                return ClassTransformer.transform2(classfileBuffer);
            case "method":
                return ClassTransformer.transform3(classfileBuffer);
            default:
                throw new RuntimeException("coverage type not included");
        }
    }
}
