@echo off
REM 项目打包脚本 (Windows) - 将项目打包为ZIP文件以便下载

setlocal enabledelayedexpansion

set PROJECT_NAME=android-webview-perf-plugin
set OUTPUT_FILE=%PROJECT_NAME%.zip

echo ==========================================
echo   项目打包脚本
echo ==========================================
echo.

REM 检查是否在项目根目录
if not exist "build.gradle.kts" (
    echo ❌ 请在项目根目录运行此脚本
    pause
    exit /b 1
)

echo 📦 开始打包项目...
echo.

REM 检查PowerShell
where powershell >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ 未找到PowerShell
    pause
    exit /b 1
)

echo 📋 复制项目文件...
echo.

REM 使用PowerShell创建ZIP
powershell -Command "& {
    $sourceDir = '.'
    $tempDir = '.temp_package'
    $excludeDirs = @('build', '.gradle', '.idea', '.git', 'bin', 'obj')

    # 创建临时目录
    if (Test-Path $tempDir) { Remove-Item -Recurse -Force $tempDir }
    New-Item -ItemType Directory -Path $tempDir | Out-Null

    # 复制核心目录
    $dirs = @('plugin', 'monitor-lib', 'app')
    foreach ($dir in $dirs) {
        if (Test-Path $dir) {
            Copy-Item -Path $dir -Destination $tempDir -Recurse -Force
            echo ✅ 复制 $dir
        } else {
            echo ⚠️  $dir 不存在
        }
    }

    # 复制配置文件
    $configFiles = @('build.gradle.kts', 'settings.gradle.kts', 'gradle.properties')
    foreach ($file in $configFiles) {
        if (Test-Path $file) {
            Copy-Item -Path $file -Destination $tempDir -Force
            echo ✅ 复制 $file
        }
    }

    # 复制文档
    $docs = @('README.md', 'BUILD_GUIDE.md', 'ONLINE_BUILD.md', 'FAQ.md', 'INTEGRATION.md', 'QUICK_REFERENCE.md', 'PROJECT_OVERVIEW.md')
    foreach ($file in $docs) {
        if (Test-Path $file) {
            Copy-Item -Path $file -Destination $tempDir -Force
            echo ✅ 复制 $file
        }
    }

    # 复制脚本
    $scripts = @('build_apk.sh', 'build_apk.bat')
    foreach ($file in $scripts) {
        if (Test-Path $file) {
            Copy-Item -Path $file -Destination $tempDir -Force
            echo ✅ 复制 $file
        }
    }

    # 删除旧的ZIP文件
    if (Test-Path '%OUTPUT_FILE%') {
        Remove-Item -Force '%OUTPUT_FILE%'
    }

    # 创建ZIP文件
    Compress-Archive -Path \"$tempDir\*\" -DestinationPath '%OUTPUT_FILE%' -CompressionLevel Optimal

    # 清理临时目录
    Remove-Item -Recurse -Force $tempDir

    # 获取文件大小
    $fileInfo = Get-Item '%OUTPUT_FILE%'
    $fileSize = [math]::Round($fileInfo.Length / 1KB, 2)
    Write-Host \"\"
    Write-Host \"📊 文件大小: $fileSize KB\"
}"

echo.
echo ==========================================
echo   打包完成！
echo ==========================================
echo.
echo 📦 文件名: %OUTPUT_FILE%
echo 📍 当前目录: %CD%
echo.
echo 💡 使用方法:
echo    1. 下载 %OUTPUT_FILE%
echo    2. 解压到本地
echo    3. 按照 BUILD_GUIDE.md 编译APK
echo.
echo 💡 或上传到在线编译服务:
echo    - GitHub Actions: 查看 ONLINE_BUILD.md
echo    - GitLab CI/CD: 查看 ONLINE_BUILD.md
echo.

pause
