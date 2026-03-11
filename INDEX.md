# 项目总览

## 📦 项目简介

这是一个基于 **字节码插桩（ASM）** 的 Android WebView 性能监控插件。

**核心特性：**
- ✅ 自动监控WebView加载URL的全生命周期耗时
- ✅ 实时可视化显示性能数据
- ✅ 零侵入 - 自动插桩，无需手动埋点
- ✅ 支持导出性能报告

---

## 📂 完整文件列表

### 📄 文档目录

| 文件 | 说明 | 必读 |
|------|------|------|
| **QUICKSTART.md** | 快速开始（3分钟上手） | ⭐ |
| **BUILD_GUIDE.md** | 完整编译指南（环境配置、常见问题） | ⭐ |
| **README.md** | 功能介绍、API文档、使用方法 | ⭐⭐ |
| **INTEGRATION.md** | 5分钟集成到现有项目 | |
| **FAQ.md** | 15个常见问题解答 | |
| **PROJECT_OVERVIEW.md** | 项目技术说明、架构设计 | |
| **QUICK_REFERENCE.md** | API速查手册 | |

### 🔧 编译脚本

| 文件 | 说明 | 平台 |
|------|------|------|
| **build_apk.sh** | Linux/macOS编译脚本 | Linux/macOS |
| **build_apk.bat** | Windows编译脚本 | Windows |
| **setup_env.sh** | 一键配置Android开发环境 | Linux |

### 📦 插桩插件 (plugin/)

| 文件 | 说明 | 作用 |
|------|------|------|
| `plugin/build.gradle.kts` | 插件构建配置 | Gradle插件定义 |
| `plugin/src/main/java/com/webview/perf/WebViewPerfPlugin.java` | 插件入口 | 注册Transform |
| `plugin/src/main/java/com/webview/perf/WebViewPerfTransform.java` | Transform实现 | 字节码转换 |
| `plugin/src/main/java/com/webview/perf/WebViewPerformanceVisitor.java` | ASM访问器 | 注入监控代码 |

**原理：** 在编译期间自动修改WebView相关类的字节码，在关键方法前后插入性能监控代码。

### 📊 监控库 (monitor-lib/)

| 文件 | 说明 | 作用 |
|------|------|------|
| `monitor-lib/build.gradle.kts` | 库构建配置 | Android Library |
| `monitor-lib/src/main/java/com/webview/perf/monitor/WebViewMonitor.kt` | 监控核心 | 数据收集、回调处理 |
| `monitor-lib/src/main/java/com/webview/perf/monitor/WebViewPerfData.kt` | 数据模型 | 性能数据结构、评分算法 |
| `monitor-lib/src/main/java/com/webview/perf/monitor/WebViewPerfVisualization.kt` | 可视化窗口 | 浮动窗口、图表显示 |

**功能：** 提供运行时性能监控和可视化展示。

### 📱 示例应用 (app/)

| 文件 | 说明 | 用途 |
|------|------|------|
| `app/build.gradle.kts` | 应用构建配置 | 应用插件、依赖监控库 |
| `app/proguard-rules.pro` | 混淆规则 | 保护监控代码不被混淆 |
| `app/src/main/AndroidManifest.xml` | 应用清单 | 声明权限、组件 |
| `app/src/main/java/com/webview/perf/demo/MainActivity.kt` | 主界面 | 基础使用示例 |
| `app/src/main/java/com/webview/perf/demo/AdvancedActivity.kt` | 高级示例 | 高级用法演示 |
| `app/src/main/res/layout/activity_main.xml` | 界面布局 | UI设计 |

**用途：** 演示如何使用性能监控插件。

---

## 🎯 工作流程

```
1. 编译阶段
   ┌─────────────────────────────────┐
   │  Gradle Build                 │
   │         ↓                      │
   │  Transform (拦截.class文件)    │
   │         ↓                      │
   │  ASM字节码注入                 │
   │         ↓                      │
   │  生成修改后的.class            │
   └─────────────────────────────────┘

2. 运行时
   ┌─────────────────────────────────┐
   │  WebView.loadUrl()            │
   │         ↓                      │
   │  WebViewMonitor记录开始时间     │
   │         ↓                      │
   │  WebViewClient.onPageFinished()│
   │         ↓                      │
   │  计算总耗时                    │
   │         ↓                      │
   │  WebViewPerfVisualization.show()│
   │         ↓                      │
   │  显示浮动窗口                  │
   └─────────────────────────────────┘
```

---

## 📊 监控的数据

### 网络阶段
```
DNS Lookup      → dnsLookupTime
  ↓
TCP Connect     → tcpConnectTime
  ↓
TLS Handshake   → tlsHandshakeTime
  ↓
TTFB           → ttfbTime
  ↓
Download       → downloadTime
```

### DOM阶段
```
DOM Parsing      → domParsingTime
  ↓
Resource Loading → resourceLoadingTime
  ↓
Rendering        → renderTime
```

---

## 🚀 快速开始（3个步骤）

### Step 1: 打开项目
```
Android Studio → Open → 选择 android-webview-perf-plugin 文件夹
```

### Step 2: 编译APK
```
菜单：Build → Build APK(s)
```

### Step 3: 安装并运行
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.webview.perf.demo/.MainActivity
```

---

## 📖 文档阅读顺序

### 第一次使用
1. **QUICKSTART.md** - 了解如何快速编译和安装
2. **BUILD_GUIDE.md** - 配置环境和解决问题

### 集成到现有项目
1. **INTEGRATION.md** - 5分钟集成指南
2. **README.md** - 详细功能和API

### 深入理解
1. **PROJECT_OVERVIEW.md** - 技术架构和实现原理
2. **FAQ.md** - 常见问题解答

### 日常参考
1. **QUICK_REFERENCE.md** - API速查手册（建议打印）

---

## 💡 使用场景

### 1. 性能分析
监控WebView加载性能，发现性能瓶颈。

### 2. 优化验证
优化前后对比性能指标。

### 3. 问题排查
定位加载慢的具体阶段（网络还是DOM）。

### 4. 质量监控
持续监控WebView性能，建立性能基线。

---

## 🔧 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 开发语言 |
| Kotlin | 1.9.10 | 监控库开发 |
| Android SDK | API 34 | 目标平台 |
| Gradle | 8.2 | 构建工具 |
| ASM | 9.6 | 字节码操作 |
| Android Gradle Plugin | 8.2.0 | 构建插件 |

---

## 📦 输出产物

编译完成后：

| 文件 | 位置 | 说明 |
|------|------|------|
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` | 调试版本（含日志） |
| Release APK | `app/build/outputs/apk/release/app-release.apk` | 发布版本（混淆） |
| AAB | `app/build/outputs/bundle/release/app-release.aab` | Google Play格式 |

---

## 🎨 界面预览

应用加载URL后，会自动显示一个悬浮窗口：

```
┌─────────────────────────────────────┐
│ 🚀 WebView性能监控                │
│ ═════════════════════════════════│
│ 📊 URL: https://www.baidu.com     │
│ ⏱️ 总耗时: 1245ms                 │
│ 🎯 评分: 85/100                   │
│                                     │
│ 网络阶段: 845ms (68%)  ████████░░ │
│ DOM解析: 200ms (16%)   ██░░░░░░░░ │
│ 资源加载: 150ms (12%)   █░░░░░░░░░ │
│ 渲染: 50ms (4%)       ░░░░░░░░░░ │
│                                     │
│ [复制报告] [导出数据] [清空]        │
└─────────────────────────────────────┘
```

---

## ✅ 验证清单

编译完成后，检查以下功能：

- [ ] APK编译成功
- [ ] 安装到设备成功
- [ ] 应用可以正常打开
- [ ] 输入URL可以加载网页
- [ ] 加载完成后弹出性能窗口
- [ ] 性能窗口显示正确的数据
- [ ] 可以点击按钮复制/导出报告

---

## 🆘 获取帮助

遇到问题？

1. 查看 **FAQ.md** - 常见问题解答
2. 查看 **BUILD_GUIDE.md** - 编译问题
3. 提交 Issue 到项目仓库
4. 运行 `adb logcat | grep WebViewPerf` 查看日志

---

**开始你的WebView性能监控之旅！** 🚀
