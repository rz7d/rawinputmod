set CFLAGS=/O2 /Ob2 /Oi /Ot /Gw /bigobj /GT /GS- /MD /I%JAVA_HOME%\include /I%JAVA_HOME%\include\win32 /LD /utf-8
cl %CFLAGS% com_github_asteriskmods_inputtweaks_rawinput_WindowsNatives.c kernel32.lib user32.lib /o inputtweaks.dll
