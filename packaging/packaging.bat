@echo off

rem Define the directory containing the class files
set bin_dir="../bin"
set target_dir="../lib"

rem Create the archive inside the target directory
jar -cvf %target_dir%/winter.jar -C %bin_dir% .

echo Created winter.jar from %bin_dir%
pause