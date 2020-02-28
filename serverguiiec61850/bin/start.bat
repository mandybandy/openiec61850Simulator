::BATCH file for windows

set BATDIR=%~dp0
set LIBDIR=%BATDIR%..\src\main\java\serverguiiec61850\libs-all

java  -Djava.ext.dirs=%LIBDIR% src.main.java.serverguiiec61850.gui.Gui.class %*

set /p eingabe="druecke was um fortzufahren: "