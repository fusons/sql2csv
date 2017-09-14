setlocal enabledelayedexpansion
set WORK_DIR=%cd%
set LIB_PATH=%WORK_DIR%\lib
set JAVA_HOME=D:\Java\jdk1.8.0_101
set CLASS_PATH=%WORK_DIR%\bin\sql2csv-fu.jar
for /f %%i in ('dir /s/b %LIB_PATH%\*.jar') do set CLASS_PATH=!CLASS_PATH!;%%i
setlocal disabledelayedexpansion
set TMP_PARMS=%1 %2
%JAVA_HOME%\bin\java -cp %CLASS_PATH% com.db2csv.db.Sql2Csv %TMP_PARMS%