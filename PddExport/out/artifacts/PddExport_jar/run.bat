@echo off
set /p firstTime=���緢��ʱ�䣨����"2020-10-28 05:34:17"��: 
echo ���緢��ʱ�� = %firstTime%
set /p endTime=������ʱ��(����"2020-10-28 05:34:17"): 
echo ������ʱ��= %endTime%
java -jar PddExport.jar %firstTime% %endTime%
pause