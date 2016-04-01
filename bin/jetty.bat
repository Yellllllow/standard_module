@echo off
echo [INFO] Use maven jetty-plugin run the project.

cd %~dp0
cd ..

set path=%MAVEN_HOME%/bin;%windir%/system32;%path%
set MAVEN_OPTS=%MAVEN_OPTS% -XX:MaxPermSize=128m
call mvn jetty:run -Djetty.port=8099

cd bin
pause