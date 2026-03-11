#!/bin/bash

# Android WebView性能监控插件 - APK编译脚本

set -e

echo "=========================================="
echo "  Android WebView性能监控插件编译脚本"
echo "=========================================="
echo ""

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 未检测到Java JDK"
    echo ""
    echo "请先安装Java JDK 17或更高版本："
    echo ""
    echo "Ubuntu/Debian:"
    echo "  sudo apt-get update"
    echo "  sudo apt-get install -y openjdk-17-jdk"
    echo ""
    echo "macOS:"
    echo "  brew install openjdk@17"
    echo ""
    echo "Windows:"
    echo "  下载并安装: https://adoptium.net/temurin/releases/"
    echo ""
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "✅ 检测到Java版本: $(java -version 2>&1 | head -n 1)"

# 检查Android SDK
if [ -z "$ANDROID_HOME" ]; then
    echo ""
    echo "❌ 未设置ANDROID_HOME环境变量"
    echo ""
    echo "请安装Android SDK并设置环境变量："
    echo ""
    echo "Linux/macOS - 添加到 ~/.bashrc 或 ~/.zshrc:"
    echo "  export ANDROID_HOME=/path/to/android/sdk"
    echo "  export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin"
    echo "  export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
    echo "  export PATH=\$PATH:\$ANDROID_HOME/build-tools/34.0.0"
    echo ""
    echo "安装方式："
    echo "  1. 下载Android Command Line Tools:"
    echo "     https://developer.android.com/studio#command-tools"
    echo ""
    echo "  2. 解压到 \$ANDROID_HOME/cmdline-tools"
    echo ""
    echo "  3. 接受许可: sdkmanager --licenses"
    echo ""
    echo "  4. 安装必要组件:"
    echo "     sdkmanager \"platform-tools\" \"platforms;android-34\" \"build-tools;34.0.0\""
    echo ""
    exit 1
fi

echo "✅ Android SDK: $ANDROID_HOME"

# 检查必要组件
if [ ! -f "$ANDROID_HOME/platform-tools/adb" ] && [ ! -f "$ANDROID_HOME/platform-tools/adb.exe" ]; then
    echo ""
    echo "❌ 未安装platform-tools"
    echo "  请运行: sdkmanager \"platform-tools\""
    exit 1
fi

echo "✅ platform-tools 已安装"

# 检查build-tools
if [ ! -d "$ANDROID_HOME/build-tools/34.0.0" ]; then
    echo ""
    echo "❌ 未安装build-tools 34.0.0"
    echo "  请运行: sdkmanager \"build-tools;34.0.0\""
    exit 1
fi

echo "✅ build-tools 34.0.0 已安装"

# 检查platform
if [ ! -d "$ANDROID_HOME/platforms/android-34" ]; then
    echo ""
    echo "❌ 未安装android-34 platform"
    echo "  请运行: sdkmanager \"platforms;android-34\""
    exit 1
fi

echo "✅ android-34 platform 已安装"

echo ""
echo "=========================================="
echo "  环境检查通过，开始编译..."
echo "=========================================="
echo ""

# 检查gradlew是否存在
if [ ! -f "./gradlew" ]; then
    echo "⚠️  未找到gradlew，尝试使用系统gradle..."
    if command -v gradle &> /dev/null; then
        echo "使用系统gradle编译..."
        gradle clean assembleDebug
    else
        echo "❌ 未找到gradle"
        echo ""
        echo "请安装Gradle 8.2或更高版本："
        echo ""
        echo "使用SDKMAN (推荐):"
        echo "  curl -s \"https://get.sdkman.io\" | bash"
        echo "  source \"\$HOME/.sdkman/bin/sdkman-init.sh\""
        echo "  sdk install gradle 8.2"
        echo ""
        echo "或手动下载:"
        echo "  https://gradle.org/releases/"
        echo ""
        exit 1
    fi
else
    echo "使用gradlew编译..."
    chmod +x ./gradlew
    ./gradlew clean assembleDebug
fi

echo ""
echo "=========================================="
echo "  编译完成！"
echo "=========================================="
echo ""
echo "APK位置:"
find ./app/build/outputs/apk/debug -name "*.apk" -type f

echo ""
echo "安装到设备（如果已连接）："
echo "  adb install -r ./app/build/outputs/apk/debug/app-debug.apk"
echo ""
