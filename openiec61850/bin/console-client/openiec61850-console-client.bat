::BATCH file for windows

set BATDIR=%~dp0
set LIBDIR=%BATDIR%..\..\build\libs-all

java  -D java.ext.dirs=%LIBDIR% com.beanit.openiec61850.app.ConsoleClient %*
