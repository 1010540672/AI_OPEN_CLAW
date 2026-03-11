#!/bin/bash

# 项目打包脚本 - 将项目打包为ZIP文件以便下载

set -e

PROJECT_NAME="android-webview-perf-plugin"
OUTPUT_FILE="${PROJECT_NAME}.zip"

echo "=========================================="
echo "  项目打包脚本"
echo "=========================================="
echo ""

# 检查是否在项目根目录
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ 请在项目根目录运行此脚本"
    exit 1
fi

echo "📦 开始打包项目..."
echo ""

# 创建临时目录
TEMP_DIR=$(mktemp -d)

echo "📋 复制项目文件..."

# 复制核心代码
cp -r plugin "$TEMP_DIR/" 2>/dev/null || echo "⚠️  plugin目录不存在"
cp -r monitor-lib "$TEMP_DIR/" 2>/dev/null || echo "⚠️  monitor-lib目录不存在"
cp -r app "$TEMP_DIR/" 2>/dev/null || echo "⚠️  app目录不存在"

# 复制配置文件
cp build.gradle.kts "$TEMP_DIR/" 2>/dev/null || true
cp settings.gradle.kts "$TEMP_DIR/" 2>/dev/null || true
cp gradle.properties "$TEMP_DIR/" 2>/dev/null || true

# 复制文档
cp README.md "$TEMP_DIR/" 2>/dev/null || echo "⚠️  README.md不存在"
cp BUILD_GUIDE.md "$TEMP_DIR/" 2>/dev/null || true
cp ONLINE_BUILD.md "$TEMP_DIR/" 2>/dev/null || true
cp FAQ.md "$TEMP_DIR/" 2>/dev/null || true
cp INTEGRATION.md "$TEMP_DIR/" 2>/dev/null || true
cp QUICK_REFERENCE.md "$TEMP_DIR/" 2>/dev/null || true
cp PROJECT_OVERVIEW.md "$TEMP_DIR/" 2>/dev/null || true

# 复制编译脚本
cp build_apk.sh "$TEMP_DIR/" 2>/dev/null || true
cp build_apk.bat "$TEMP_DIR/" 2>/dev/null || true

echo ""
echo "📋 已包含的文件:"
find "$TEMP_DIR" -type f | sort | sed 's|'"$TEMP_DIR"'||'

echo ""
echo "📦 正在创建ZIP文件..."

# 删除旧的ZIP文件
rm -f "$OUTPUT_FILE"

# 创建ZIP文件
cd "$TEMP_DIR"
zip -r "../$OUTPUT_FILE" . -x "*/build/*" "*/.gradle/*" "*/.idea/*" ".git/*" "*/local.properties" "*/.DS_Store"
cd - > /dev/null

# 清理临时目录
rm -rf "$TEMP_DIR"

# 获取文件大小
FILE_SIZE=$(du -h "$OUTPUT_FILE" | cut -f1)

echo ""
echo "=========================================="
echo "  打包完成！"
echo "=========================================="
echo ""
echo "📦 文件名: $OUTPUT_FILE"
echo "📊 文件大小: $FILE_SIZE"
echo "📍 当前目录: $(pwd)"
echo ""
echo "💡 使用方法:"
echo "   1. 下载 $OUTPUT_FILE"
echo "   2. 解压到本地"
echo "   3. 按照 BUILD_GUIDE.md 编译APK"
echo ""
echo "💡 或上传到在线编译服务:"
echo "   - GitHub Actions: 查看 ONLINE_BUILD.md"
echo "   - GitLab CI/CD: 查看 ONLINE_BUILD.md"
echo ""
