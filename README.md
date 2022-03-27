# jacoconut
毕业设计——java覆盖工具

# Something You Should know
+ this tool is designed for projects built on **maven** & **junit**
+ ensure surefire plugin is in your project
+ for jdk 1.7 and jdk 1.8, you should add extra jvm args to skip stackmap check
  + for jdk1.7( -XX:-UseSplitVerifier )
  + for jdk1.8( -noverify )
  + or you could add them both

