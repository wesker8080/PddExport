@echo off
set /p firstTime=最早发货时间（例如"2020-10-28 05:34:17"）: 
echo 最早发货时间 = %firstTime%
set /p endTime=最晚发货时间(例如"2020-10-28 05:34:17"): 
echo 最晚发货时间= %endTime%
java -jar PddExport.jar %firstTime% %endTime%
pause