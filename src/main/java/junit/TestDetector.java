package junit;

import api.JacoconutApi;
import org.apache.maven.it.VerificationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestDetector {
    private TestDetector.ExtendedClassLoader sec;
    private String project;

    public TestDetector(String project) throws MalformedURLException, FileNotFoundException {
        if(!Files.isDirectory(Paths.get(project ,"target","test-classes"))){
            throw new FileNotFoundException(String.format("Directory %s not found", Paths.get(project ,"target","test-classes").toAbsolutePath()));
        }
        this.project=Paths.get(project).toAbsolutePath().toString();
        sec=new TestDetector.ExtendedClassLoader(new URL[0], JacoconutApi.class.getClassLoader());
        sec.addURL(Paths.get(project,"target","test-classes").toUri().toURL());
        sec.addURL(Paths.get(project,"target","classes").toUri().toURL());
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
                            testClazz = sec.loadClass(className);
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
        return m;
    }

    public static class ExtendedClassLoader extends URLClassLoader {
        public ExtendedClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public void addURL(URL url) {
            super.addURL(url);
        }
    }

    public static void main(String[] args) {
        try {
            Map<String,List<String>> m=new TestDetector("C:\\Users\\tom\\Desktop\\junittest").detectAllJunitTests();
            TestDriver t=new TestDriver("C:\\Users\\tom\\Desktop\\junittest");

            for (String clazz:m.keySet()){
                for(String method:m.get(clazz)){
                    t.run(clazz,method);
                }
            }
        } catch (IOException | VerificationException e) {
            e.printStackTrace();
        }
    }

}
