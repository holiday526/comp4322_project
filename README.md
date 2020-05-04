# comp4322_project

library required:
org.jgrapht:jgrapht-ext:1.4.0
org.jgrapht:jgrapht-core:1.4.0

jar file required:
forms_rt-7.0.3.jar

Import project to IntelliJ
Project structure > Project setting > Artifacts
Create artifacts > JAR > Empty
check included in project build
add:
org.jgrapht:jgrapht-ext:1.4.0
org.jgrapht:jgrapht-core:1.4.0
forms_rt-7.0.3.jar

build

in <project_name>/out/artifacts/<jar_directory>
unzip jar files

before run the LSRCompute command > locate the routes.lsa file

run LSRCompute using command:
java LSRCompute <route filename> <start node> <SS|CA>
  
------
  
NEW added <b>comp4322_project_no_ide</b>\
javac LSRCompute.java\
java LSRCompute <route filename>\
\
route file now should be located at same directory with execute file (.class)
