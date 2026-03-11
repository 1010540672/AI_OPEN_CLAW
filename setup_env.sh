#!/bin/bash

# 下载并配置Android开发环境（Ubuntu/Debian）

set -e

echo "=========================================="
echo "  Android开发环境一键配置"
echo "=========================================="
echo ""

# 检测系统
if [[ "$OSTYPE" != "linux-gnu"* ]]; then
    echo "❌ 此脚本仅适用于Linux系统"
    echo "   Windows用户请使用 Android Studio"
    echo "   macOS用户请参考文档"
    exit 1
fi

# 安装Java JDK
echo "1️⃣  检查Java JDK..."
if ! command -v java &> /dev/null; then
    echo "   安装OpenJDK 17..."
    sudo apt-get update
    sudo apt-get install -y openjdk-17-jdk
else
    echo "   ✅ Java已安装: $(java -version 2>&1 | head -n 1)"
fi

# 设置JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
    echo "   设置JAVA_HOME..."
    JAVA_PATH=$(readlink -f $(which java) | sed "s:bin/java::")
    echo "export JAVA_HOME=$JAVA_PATH" >> ~/.bashrc
    export JAVA_HOME=$JAVA_PATH
fi

# 下载并安装Android Command Line Tools
echo ""
echo "2️⃣  下载Android Command Line Tools..."
ANDROID_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip"
TOOL_DIR="$HOME/Android/cmdline-tools"

mkdir -p "$TOOL_DIR"
cd "$TOOL_DIR"

if [ ! -f "cmdline-tools.zip" ]; then
    echo "   下载中..."
    curl -o cmdline-tools.zip "$ANDROID_TOOLS_URL"
fi

echo "   解压中..."
unzip -o cmdline-tools.zip
mkdir -p latest
mv cmdline-tools/* latest/
rm cmdline-tools.zip

cd -

# 设置环境变量
echo ""
echo "3️⃣  配置环境变量..."
SHELL_CONFIG="$HOME/.bashrc"

if ! grep -q "ANDROID_HOME" "$SHELL_CONFIG"; then
    echo "" >> "$SHELL_CONFIG"
    echo "# Android SDK" >> "$SHELL_CONFIG"
    echo "export ANDROID_HOME=$HOME/Android" >> "$SHELL_CONFIG"
    echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin" >> "$SHELL_CONFIG"
    echo "export PATH=\$PATH:\$ANDROID_HOME/platform-tools" >> "$SHELL_CONFIG"
    echo "export PATH=\$PATH:\$ANDROID_HOME/build-tools/34.0.0" >> "$SHELL_CONFIG"
fi

export ANDROID_HOME=$HOME/Android
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools

echo "   ✅ ANDROID_HOME=$ANDROID_HOME"

# 接受许可协议
echo ""
echo "4️⃣  接受许可协议..."
yes | sdkmanager --licenses > /dev/null

# 安装必要组件
echo ""
echo "5️⃣  安装Android SDK组件..."
echo "   安装platform-tools..."
sdkmanager "platform-tools" > /dev/null

echo "   安装platform android-34..."
sdkmanager "platforms;android-34" > /dev/null

echo "   安装build-tools 34.0.0..."
sdkmanager "build-tools;34.0.0" > /dev/null

# 安装Gradle
echo ""
echo "6️⃣  安装Gradle 8.2..."
if ! command -v gradle &> /dev/null; then
    GRADLE_VERSION="8.2"
    GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

    mkdir -p ~/gradle
    cd ~/gradle

    if [ ! -f "gradle-${GRADLE_VERSION}-bin.zip" ]; then
        echo "   下载Gradle ${GRADLE_VERSION}..."
        curl -o "gradle-${GRADLE_VERSION}-bin.zip" "$GRADLE_URL"
    fi

    echo "   解压Gradle..."
    unzip -o "gradle-${GRADLE_VERSION}-bin.zip"

    if ! grep -q "gradle" "$SHELL_CONFIG"; then
        echo "" >> "$SHELL_CONFIG"
        echo "# Gradle" >> "$SHELL_CONFIG"
        echo "export GRADLE_HOME=~/gradle/gradle-${GRADLE_VERSION}" >> "$SHELL_CONFIG"
        echo "export PATH=\$PATH:\$GRADLE_HOME/bin" >> "$SHELL_CONFIG"
    fi

    export GRADLE_HOME=~/gradle/gradle-${GRADLE_VERSION}
    export PATH=$PATH:$GRADLE_HOME/bin
    cd -
else
    echo "   ✅ Gradle已安装: $(gradle --version | grep Gradle | head -n 1)"
fi

echo ""
echo "=========================================="
echo "  ✅ 环境配置完成！"
echo "=========================================="
echo ""
echo "请运行以下命令使环境变量生效："
echo ""
echo "  source ~/.bashrc"
echo ""
echo "然后进入项目目录编译APK："
echo ""
echo "  cd android-webview-perf-plugin"
echo "  ./build_apk.sh"
echo ""
