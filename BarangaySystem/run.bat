@echo off
echo Compiling project...
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar" -d out src/Main.java src/model/*.java src/db/*.java src/gui/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Launching application...
java -cp "out;lib/sqlite-jdbc-3.45.1.0.jar;lib/slf4j-api-2.0.9.jar;lib/slf4j-simple-2.0.9.jar" Main
pause
