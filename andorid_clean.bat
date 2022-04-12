
@echo off build by tino
@echo build by tino
@echo 正在清除安卓项目垃圾文件，请稍等...... 

@echo 删除class和dex文件

del *.class *.dex *.iml *.hprof *.hprof/s /f /a /q
rd /s /q .gradle

@echo 遍历工程文件，删除所有build文件夹

:: /s 代表删除其中的子目录， /q 表示删除目录树时不提示确认，
 
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\build"')do rd /s /q "%%a" 2>nul

@echo 遍历工程文件，删除所有gradle文件夹
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\gradle"')do rd /s /q "%%a" 2>nul

@echo 遍历工程文件，删除所有.idea文件夹
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\.idea"')do rd /s /q "%%a" 2>nul
