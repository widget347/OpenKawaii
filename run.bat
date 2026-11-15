@echo off
setlocal
cd /d "%~dp0"
title Kawaii

rem Usage: run.bat [username]     (default username: Player)
set "NAME=%~1"
if "%NAME%"=="" set "NAME=Player"

if not exist "build\classes\net\minecraft\client\main\Main.class" call build.bat
if not exist "build\classes\net\minecraft\client\main\Main.class" exit /b 1

set "LIB=%~dp0lib"
set "GAME=%~dp0gamedir"

where java >nul 2>nul
if errorlevel 1 goto nojava
set "JV="
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JV=%%~v"
if "%JV%"=="" goto nojava
for /f "delims=.-+" %%a in ("%JV%") do set "JM=%%a"
if "%JM%"=="1" goto nojava
if %JM% LSS 17 goto nojava
goto javaok

:nojava
echo Java 17 or newer ^(64-bit^) is required and was not found.
pause
exit /b 1

:javaok
if not exist "%GAME%" mkdir "%GAME%"
if not exist "%GAME%\assets" mkdir "%GAME%\assets"

rem OptiFine "Fast Render" disables the framebuffer that ESP and Blur need: force it off
if exist "%GAME%\optionsof.txt" (
  findstr /v /b /c:"ofFastRender:" "%GAME%\optionsof.txt" > "%GAME%\optionsof.tmp"
) else (
  type nul > "%GAME%\optionsof.tmp"
)
echo ofFastRender:false>> "%GAME%\optionsof.tmp"
move /y "%GAME%\optionsof.tmp" "%GAME%\optionsof.txt" >nul

echo Starting Kawaii as %NAME% ...
java -Xmx2G -Djava.library.path="%LIB%" -Dorg.lwjgl.librarypath="%LIB%" -Dnet.java.games.input.librarypath="%LIB%" ^
  --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED ^
  -cp "build\classes;lib" net.minecraft.client.main.Main ^
  --username "%NAME%" --version Kawaii --gameDir "%GAME%" --assetsDir "%GAME%\assets" --assetIndex 1.8 ^
  --accessToken 0 --uuid 00000000000000000000000000000000 --userProperties {} --userType legacy
if errorlevel 1 (
  echo.
  echo The client exited with an error ^(see above^).
  pause
)
endlocal
