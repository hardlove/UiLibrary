
@echo off build by tino
@echo build by tino
@echo ���������׿��Ŀ�����ļ������Ե�...... 

@echo ɾ��class��dex�ļ�

del *.class *.dex *.iml *.hprof *.hprof/s /f /a /q
rd /s /q .gradle

@echo ���������ļ���ɾ������build�ļ���

:: /s ����ɾ�����е���Ŀ¼�� /q ��ʾɾ��Ŀ¼��ʱ����ʾȷ�ϣ�
 
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\build"')do rd /s /q "%%a" 2>nul

@echo ���������ļ���ɾ������gradle�ļ���
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\gradle"')do rd /s /q "%%a" 2>nul

@echo ���������ļ���ɾ������.idea�ļ���
for /f "delims=" %%a in ('dir /b/s/ad ^|findstr /c ".\.idea"')do rd /s /q "%%a" 2>nul
