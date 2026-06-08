@echo off
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar" -d out src/Main.java src/model/*.java src/db/*.java src/gui/*.java
echo Compilation complete!
pause
