# 🚀 快速开始编译APK

## 最简单的方式（推荐）

### 1. 使用Android Studio编译

**步骤：**

1. 下载并安装 [Android Studio](https://developer.android.com/studio)
2. 打开 Android Studio
3. 选择 "Open" → 选择 `android-webview-perf-plugin` 文件夹
4. 等待Gradle同步完成（右下角）
5. 点击菜单：`Build` → `Build APK(s)`
6. 编译完成后点击通知中的 "locate"
7. APK文件位置：`app/build/outputs/apk/debug/app-debug.apk`

**就是这么简单！** ✅

---

## 命令行方式

### Windows
```cmd
cd android-webview-perf-plugin
build_apk.bat
```

### Linux/macOS
```bash
cd android-webview-perf-plugin
chmod +x setup_env.sh  # 首次运行，配置环境
./setup_env.sh
source ~/.bashrc
./build_apk.sh
```

---

## 安装到手机

```bash
# 1. 手机开启USB调试
# 设置 → 开发者选项 → USB调试

# 2. 连接手机后安装
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 3. 启动应用
adb shell am start -n com.webview.perf.demo/.MainActivity
```

---

## 📖 详细文档

- **BUILD_GUIDE.md** - 完整编译指南（环境配置、常见问题）
- **README.md** - 功能介绍和使用说明
- **INTEGRATION.md** - 集成到现有项目
- **FAQ.md** - 常见问题解答

---

## 📱 应用功能

1. 输入URL（如：https://www.baidu.com）
2. 点击"加载"
3. 页面加载完成后，**自动弹出性能监控窗口**
4. 查看详细的加载时间、性能评分
5. 导出性能报告

---

## 🎯 性能监控指标

- 🌐 DNS查询、TCP连接、TLS握手
- ⏱️ TTFB（首字节时间）
- 📥 内容下载
- 📄 DOM解析
- 🎨 资源加载和渲染
- ⭐ 自动性能评分（0-100分）

---

## ⚠️ 系统要求

- **Android版本**：5.0 (API 21) 及以上
- **内存**：建议2GB以上
- **网络**：需要网络权限

---

## 💡 提示

- Debug版本包含日志，方便调试
- Release版本体积更小，性能更好
- 第一次编译可能较慢（需要下载依赖）
- 建议使用最新版Android Studio

---

**遇到问题？** 查看 [BUILD_GUIDE.md](BUILD_GUIDE.md) 获取详细帮助。

**开始使用吧！** 🎉
