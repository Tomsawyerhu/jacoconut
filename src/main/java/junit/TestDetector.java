package junit;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import storage.Storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class TestDetector {
    private String project;

    public TestDetector(String project) throws MalformedURLException, FileNotFoundException {
        if(!Files.isDirectory(Paths.get(project ,"target","test-classes"))){
            throw new FileNotFoundException(String.format("Directory %s not found", Paths.get(project ,"target","test-classes").toAbsolutePath()));
        }
        this.project=Paths.get(project).toAbsolutePath().toString();
    }

    private TestDetector(){}

    public void detectAllJunitTests()  {
        final String finalProject = project;
        try {
            Files
                    .walk(Paths.get(finalProject,"target","test-classes"))
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .forEach(path -> {
                        try {
                            FileInputStream stream=new FileInputStream(path.toAbsolutePath().toString());
                            ClassReader reader=new ClassReader(stream);
                            reader.accept(new TestDetectClassAdapter(null), org.objectweb.asm.ClassReader.SKIP_FRAMES);
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class TestDetectClassAdapter extends ClassVisitor {
        String className;

        public TestDetectClassAdapter(ClassVisitor cv) {
            super(458752,cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className=name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if(desc.equals("()V")&&access==ACC_PUBLIC){
                if(name.startsWith("test")){
                    Storage.tests.get().putIfAbsent(className.replace("/","."),new ArrayList<>());
                    Storage.tests.get().get(className.replace("/",".")).add(name);
                }else{
                    return new TestDetectMethodAdapter(className,name);
                }
            }
            return super.visitMethod(access,name,desc,signature,exceptions);
        }
    }

    private static class TestDetectMethodAdapter extends MethodVisitor{
        String n1;
        String n2;
        public TestDetectMethodAdapter(String className,String methodName) {
            super(458752);
            n1=className;
            n2=methodName;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            if(s.equals("Lorg/junit/Test;")){
                Storage.tests.get().putIfAbsent(n1.replace("/","."),new ArrayList<>());
                Storage.tests.get().get(n1.replace("/",".")).add(n2);
            }
            return super.visitAnnotation(s, b);
        }
    }
}
