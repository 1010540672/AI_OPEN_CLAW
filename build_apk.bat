@echo off
REM Android WebView性能监控插件 - APK编译脚本 (Windows)

echo ==========================================
echo   Android WebView性能监控插件编译脚本
echo ==========================================
echo.

REM 检查Java环境
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 未检测到Java JDK
    echo.
    echo 请先安装Java JDK 17或更高版本：
    echo.
    echo 下载地址: https://adoptium.net/temurin/releases/
    echo.
    pause
    exit /b 1
)

echo ✅ 检测到Java
java -version

REM 检查Android SDK
if "%ANDROID_HOME%"=="" (
    echo.
    echo ❌ 未设置ANDROID_HOME环境变量
    echo.
    echo 请设置系统环境变量：
    echo.
    echo ANDROID_HOME = C:\path\to\Android\Sdk
    echo.
    echo 并将以下路径添加到Path：
    echo %%ANDROID_HOME%%\cmdline-tools\latest\bin
    echo %%ANDROID_HOME%%\platform-tools
    echo %%ANDROID_HOME%%\build-tools\34.0.0
    echo.
    echo 安装方式：
    echo 1. 下载Android Studio: https://developer.android.com/studio
    echo 2. 安装后打开 SDK Manager
    echo 3. 安装：Android 14 (API 34), SDK Build-Tools 34.0.0
    echo 4. 接受所有许可协议
    echo.
    pause
    exit /b 1
)

echo ✅ Android SDK: %ANDROID_HOME%

REM 检查必要组件
if not exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    echo.
    echo ❌ 未安装platform-tools
    echo   请在 Android Studio 的 SDK Manager 中安装
    pause
    exit /b 1
)

echo ✅ platform-tools 已安装

if not exist "%ANDROID_HOME%\build-tools\34.0.0" (
    echo.
    echo ❌ 未安装build-tools 34.0.0
    echo   请在 Android Studio 的 SDK Manager 中安装
    pause
    exit /b 1
)

echo ✅ build-tools 34.0.0 已安装

if not exist "%ANDROID_HOME%\platforms\android-34" (
    echo.
    echo ❌ 未安装android-34 platform
    echo   请在 Android Studio 的 SDK Manager 中安装
    pause
    exit /b 1
)

echo ✅ android-34 platform 已安装

echo.
echo ==========================================
echo   环境检查通过，开始编译...
echo ==========================================
echo.

REM 检查gradlew是否存在
if not exist "gradlew.bat" (
    echo ⚠️  未找到gradlew.bat，尝试使用系统gradle...
    where gradle >nul 2>nul
    if %ERRORLEVEL% NEQ 0 (
        echo ❌ 未找到gradle
        echo.
        echo 请安装Gradle 8.2或更高版本：
        echo.
        echo 下载地址: https://gradle.org/releases/
        echo 或使用SDKMAN: https://sdkman.io/
        echo.
        pause
        exit /b 1
    )
    echo 使用系统gradle编译...
    gradle clean assembleDebug
) else (
    echo 使用gradlew编译...
    call gradlew.bat clean assembleDebug
)

echo.
echo ==========================================
echo   编译完成！
echo ==========================================
echo.
echo APK位置:
dir /s /b .\app\build\outputs\apk\debug\*.apk

echo.
echo 安装到设备（如果已连接）：
echo   adb install -r .\app\build\outputs\apk\debug\app-debug.apk
echo.

pause
