#!/bin/bash

# GitHub推送脚本
# 使用新创建的token进行推送

set -e

echo "=========================================="
echo "  推送代码到GitHub"
echo "=========================================="
echo ""

# 检查是否在正确的目录
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ 错误：请在项目根目录运行此脚本"
    exit 1
fi

echo "✅ 当前目录：$(pwd)"
echo ""

# 设置远程仓库（请替换为你的仓库地址）
REPO_URL="https://github.com/你的用户名/你的仓库名.git"

echo "📋 配置信息："
echo "   仓库地址：$REPO_URL"
echo "   分支：main"
echo ""

# 询问用户确认
read -p "请输入你的GitHub仓库地址（例如：https://github.com/username/repo.git）: " input_url
if [ ! -z "$input_url" ]; then
    REPO_URL=$input_url
fi

echo ""
echo "准备推送到：$REPO_URL"
echo ""

# 添加远程仓库
git remote add origin "$REPO_URL" 2>/dev/null || git remote set-url origin "$REPO_URL"

echo "✅ 远程仓库已配置"
echo ""

# 推送到main分支
echo "🚀 开始推送..."
echo ""
echo "提示：GitHub会要求你输入认证信息"
echo "   Username: 你的GitHub用户名"
echo "   Password: 你的Personal Access Token（不是登录密码！）"
echo ""

git branch -M main
git push -u origin main

echo ""
echo "=========================================="
echo "  ✅ 推送成功！"
echo "=========================================="
echo ""
echo "仓库地址：$REPO_URL"
echo ""
