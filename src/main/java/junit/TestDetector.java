package junit;
import storage.Storage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestDetector {

    private String project;

    public TestDetector(String project) {
        this.project=Paths.get(project).toAbsolutePath().toString();
    }

    private TestDetector(){}

    public Map<String, List<String>> detectAllJunitTests()  {
        Map<String, List<String>> m=new HashMap<>();

        final String finalProject = project;
        try {
            Files
                    .walk(Paths.get(project,"target","test-classes"))
                    .filter(path -> path.getFileName().toString().endsWith(".class"))
                    .forEach(path -> {
                        String className=Paths.get(finalProject,"target","test-classes").relativize(path).toString().replace(".class","").replace("/",".").replace("\\",".");
                        Class<?> testClazz= null;
                        try {
                            testClazz = Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        if (testClazz != null) {
                            for(Method method:testClazz.getMethods()){
                                // @org.junit.Test
                                // Public void test()
                                if( Modifier.isPublic(method.getModifiers())
                                        && method.getReturnType().equals(Void.TYPE)
                                        && method.getParameterTypes().length==0
                                        && (method.getName().startsWith("test")||
                                        Arrays
                                                .stream(method.getAnnotations())
                                                .anyMatch(
                                                        annotation -> annotation.annotationType().getName().equals("org.junit.Test")
                                                ))
                                ){
                                    if(!m.containsKey(className)){
                                        m.put(className,new ArrayList<>());
                                    }
                                    m.get(className).add(method.getName());
                                }
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Storage.tests.set(m);
        return m;
    }
}
