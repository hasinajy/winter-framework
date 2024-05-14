@echo off

set test_dir="C:\Users\Hasina\Desktop\School\Mr Naina\Projects\Winter Framework\test-project\lib"

rem Define the directory containing the class files
set bin_dir="../bin"
set target_dir="../lib"

rem Create the archive inside the target directory
jar -cvf %target_dir%/winter.jar -C %bin_dir% .

rem Send the library to the target directory
del %test_dir%\winter.jar
echo D | xcopy /q/s/y "..\lib\winter.jar" %test_dir%

echo Created winter.jar from %bin_dir%
pause