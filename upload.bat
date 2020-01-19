@echo OFF
git add .
echo Abbrechen mit Strg-C 
set /p commitmsg=Commit Nachricht eingeben:
if "%commitmsg%"=="" %commitmsg%="autopush"
git commit -m "%commitmsg%"
git push