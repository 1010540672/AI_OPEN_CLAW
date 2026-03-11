#!/bin/bash

# 本地编译APK指南
# 在本地机器上运行此脚本快速编译APK

set -e

echo "=========================================="
echo "  Android WebView性能监控插件 - 本地编译"
echo "=========================================="
echo ""

# 检查Java
if ! command -v java &> /dev/null; then
    echo "❌ 请先安装Java 17"
    echo "   下载: https://adoptium.net/temurin/releases/?version=17"
    exit 1
fi

# 检查Android SDK
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️  未设置ANDROID_HOME"
    echo "   建议安装Android Studio: https://developer.android.com/studio"
fi

# 使用Gradle Wrapper编译
echo "🚀 开始编译..."
echo ""

if [ -f "./gradlew" ]; then
    ./gradlew clean assembleDebug
else
    echo "⚠️  未找到gradlew，使用系统gradle..."
    gradle clean assembleDebug
fi

echo ""
echo "=========================================="
echo "  ✅ 编译完成！"
echo "=========================================="
echo ""

APK_PATH=$(find ./app/build/outputs/apk/debug -name "*.apk" -type f | head -1)

if [ -f "$APK_PATH" ]; then
    echo "📱 APK文件: $APK_PATH"
    echo "📊 文件大小: $(du -h "$APK_PATH" | cut -f1)"
    echo ""
    echo "安装到设备:"
    echo "  adb install -r \"$APK_PATH\""
else
    echo "❌ 未找到APK文件"
    echo "   请检查编译日志"
fi

echo ""
