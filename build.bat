@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
  echo JDK 17 or newer is required ^(javac not found^).
  pause
  exit /b 1
)

if exist build rmdir /s /q build
mkdir build\classes

powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName.Replace('\','/') } | Set-Content -Encoding ASCII build/sources.txt"

echo Compiling...
javac -proc:none -nowarn -encoding UTF-8 --release 17 -cp lib -d build\classes @build\sources.txt
if errorlevel 1 (
  echo Compilation failed.
  pause
  exit /b 1
)

echo Built build\classes
endlocal
