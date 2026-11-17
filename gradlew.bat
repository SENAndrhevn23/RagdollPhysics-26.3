@echo off
setlocal
set "GRADLE_VERSION=9.8.2"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "BOOTSTRAP_DIR=%GRADLE_USER_HOME%\ragdoll-bootstrap"
set "GRADLE_HOME=%BOOTSTRAP_DIR%\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"

if exist "%GRADLE_BIN%" goto run_gradle

if not exist "%BOOTSTRAP_DIR%" mkdir "%BOOTSTRAP_DIR%"
set "GRADLE_VERSION=%GRADLE_VERSION%"
set "BOOTSTRAP_DIR=%BOOTSTRAP_DIR%"
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $archive=Join-Path $env:BOOTSTRAP_DIR ('gradle-' + $env:GRADLE_VERSION + '-bin.zip'); Invoke-WebRequest -Uri ('https://services.gradle.org/distributions/gradle-' + $env:GRADLE_VERSION + '-bin.zip') -OutFile $archive; Expand-Archive -LiteralPath $archive -DestinationPath $env:BOOTSTRAP_DIR -Force; Remove-Item $archive"
if errorlevel 1 exit /b %errorlevel%

:run_gradle
call "%GRADLE_BIN%" %*
exit /b %errorlevel%
