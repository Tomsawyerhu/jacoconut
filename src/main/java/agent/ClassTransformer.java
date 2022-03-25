package agent;

import coverage.classAdapter.CoverageClassAdapter;
import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import storage.Property;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassTransformer  implements ClassFileTransformer {
    private static Logger logger = Logger.getLogger(ClassTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(className==null){
            return classfileBuffer;
        }

        if(loader!=ClassLoader.getSystemClassLoader()){
            return classfileBuffer;
        }

        String classNameWithDots = className.replace('/', '.');

        //whitelist
        if(Property.PROJECT_PREFIX!=null&&!className.startsWith(Property.PROJECT_PREFIX.replace(".","/"))){
            return classfileBuffer;
        }

        //exclude test
        if(Jagent.excludes.contains(classNameWithDots)){
            return classfileBuffer;
        }

        logger.info("transformer for class: " + className);

        byte[] result;
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(
                org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = writer;

        cv = new CoverageClassAdapter(cv);
        reader.accept(cv, ClassReader.SKIP_FRAMES);
        result = writer.toByteArray();
        return result;
    }
}
