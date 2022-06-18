# jacoconut
毕业设计——java覆盖工具

# Something You Should know
+ this tool is designed for projects built on **maven** & **junit**
+ ensure surefire plugin is in your project
+ for jdk 1.7 and jdk 1.8 and after, you should add extra jvm args to skip stackmap check
  + for jdk1.7( -XX:-UseSplitVerifier )
  + for jdk1.8( -noverify )
  + or you could add them both
```
   <!-- for jdk 1.8 and after-->
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-surefire-plugin</artifactId>
       <version>3.0.0-M5</version>
       <configuration>
            <argLine>-noverify</argLine>
       </configuration>
   </plugin>
   
   <!-- for jdk 1.7 -->
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-surefire-plugin</artifactId>
       <version>3.0.0-M5</version>
       <configuration>
            <argLine>-XX:-UseSplitVerifier</argLine>
       </configuration>
   </plugin>
```
+ set command line args for test, use mvn test -DargLine
can also use surefire, set in the pom.xml
```
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M5</version>
                <configuration>
                    <argLine>
                        -javaagent:"D:\testagent\lib\jacoconut-1.0-SNAPSHOT-jar-with-dependencies.jar"
                        -Dproject.prefix=com.yy
                        -Djacoconut.core=line
                    </argLine>
                </configuration>
            </plugin>

```
