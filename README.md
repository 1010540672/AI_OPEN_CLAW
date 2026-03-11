# Android WebView 性能检测插件

## 📋 项目概述

这是一个基于 **字节码插桩（ASM）** 的 Android WebView 性能监控插件，能够自动监控 WebView 加载 URL 的全生命周期耗时，并提供**实时可视化**显示。

### 核心特性

✅ **字节码插桩** - 自动注入性能监控代码，无需手动埋点
✅ **全生命周期监控** - DNS、TCP、TLS、TTFB、下载、解析、渲染
✅ **实时可视化** - 浮动窗口实时显示性能数据
✅ **性能评分** - 自动计算0-100分性能评分
✅ **数据导出** - 支持导出JSON格式性能报告
✅ **零侵入** - 只需应用插件，代码自动注入

---

## 📁 项目结构

```
android-webview-perf-plugin/
├── plugin/                    # Gradle Transform 插桩插件
│   └── src/main/java/com/webview/perf/
│       ├── WebViewPerfPlugin.java          # 插件入口
│       ├── WebViewPerfTransform.java       # Transform 实现
│       └── WebViewPerformanceVisitor.java  # ASM 字节码访问器
│
├── monitor-lib/              # 监控核心库
│   └── src/main/java/com/webview/perf/monitor/
│       ├── WebViewMonitor.kt                # 监控器核心类
│       ├── WebViewPerfData.kt               # 性能数据模型
│       └── WebViewPerfVisualization.kt      # 可视化窗口
│
├── app/                       # 示例应用
│   └── src/main/java/com/webview/perf/demo/
│       └── MainActivity.kt                  # 使用示例
│
├── build.gradle.kts          # 项目根构建文件
└── settings.gradle.kts       # 项目配置
```

---

## 🚀 快速开始

### 1. 引入插件

在项目根目录的 `settings.gradle.kts` 中添加：

```kotlin
include(":plugin", ":monitor-lib", ":app")
```

### 2. 应用插件到你的应用模块

在你的应用模块的 `build.gradle.kts` 中：

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
}

// 添加插件依赖
buildscript {
    dependencies {
        classpath(project(":plugin"))
    }
}

// 应用插件
apply {
    plugin("com.webview.perf.plugin")
}

// 依赖监控库
dependencies {
    implementation(project(":monitor-lib"))
}

// 配置插件选项
configure<com.webview.perf.WebViewPerfExtension> {
    enableTrace = true              // 启用性能追踪
    enableVisualization = true       // 启用可视化窗口
    packageName = "your.package.name" // 你的应用包名
}
```

### 3. 初始化监控器

在 `Application` 或 `MainActivity` 的 `onCreate` 中初始化：

```kotlin
import com.webview.perf.monitor.WebViewMonitor

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WebViewMonitor.init(this)
        WebViewMonitor.setEnabled(true)
        WebViewMonitor.setVisualizationEnabled(true)
    }
}
```

### 4. 正常使用 WebView

**无需任何额外代码！** 插件会自动监控所有 WebView 的 `loadUrl` 调用：

```kotlin
webView.loadUrl("https://www.example.com")
```

---

## 📊 监控的性能指标

### 网络阶段
- **DNS查询** - 域名解析耗时
- **TCP连接** - 建立TCP连接耗时
- **TLS握手** - SSL/TLS握手耗时
- **TTFB** - Time to First Byte（首字节时间）
- **内容下载** - 资源下载耗时

### DOM阶段
- **DOM解析** - HTML解析耗时
- **资源加载** - CSS/JS/图片等资源加载耗时
- **渲染** - 页面绘制到屏幕耗时

### 总体指标
- **总加载时间** - 从loadUrl到页面完全加载
- **性能评分** - 0-100分（优秀/良好/一般/较差/极差）

---

## 🎨 可视化功能

加载URL后，会自动显示一个悬浮窗口，包含：

### 顶部信息
- URL（截断显示）
- 总耗时
- 性能评分

### 进度条
- 网络阶段（蓝色）
- DOM解析（橙色）
- 资源加载（紫色）
- 渲染（绿色）

### 详细数据
- 每个阶段的精确耗时
- 百分占比

### 操作按钮
- **复制报告** - 复制详细报告到剪贴板
- **导出数据** - 导出JSON文件到本地
- **清空** - 清空监控数据并关闭窗口

---

## 🔧 高级用法

### 手动获取性能数据

```kotlin
// 获取所有性能数据
val allData = WebViewMonitor.getAllPerfData()

// 获取特定URL的数据
val specificData = WebViewMonitor.getPerfDataByUrl("https://example.com")

// 打印详细报告
val data = allData.last()
println(data.toDetailedString())

// 获取性能评分
val score = data.getPerformanceScore()
val level = data.getPerformanceLevel() // EXCELLENT/GOOD/FAIR/POOR/CRITICAL
```

### 手动更新网络计时

如果你的WebView使用自定义的 `WebViewClient`，可以手动更新网络耗时：

```kotlin
webView.webViewClient = object : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        val sessionId = WebViewMonitor.generateSessionId(url ?: "")
        WebViewMonitor.updateNetworkTiming(
            sessionId,
            NetworkStage.DNS_LOOKUP,
            150 // DNS耗时150ms
        )
        super.onPageStarted(view, url, favicon)
    }
}
```

### 控制显示/隐藏

```kotlin
// 启用监控
WebViewMonitor.setEnabled(true)

// 启用可视化窗口
WebViewMonitor.setVisualizationEnabled(true)

// 禁用监控
WebViewMonitor.setEnabled(false)

// 禁用可视化窗口
WebViewMonitor.setVisualizationEnabled(false)
```

---

## 📈 性能评分标准

| 评分 | 等级 | 范围 | 颜色 |
|------|------|------|------|
| 90-100 | 优秀 | < 1000ms | 🟢 绿色 |
| 75-89 | 良好 | 1000-2000ms | 🟢 浅绿 |
| 60-74 | 一般 | 2000-3000ms | 🟡 黄色 |
| 40-59 | 较差 | 3000-5000ms | 🟠 橙色 |
| 0-39 | 极差 | > 5000ms | 🔴 红色 |

---

## 🛠️ 技术实现

### 字节码插桩原理

1. **Gradle Transform** - 在编译期间拦截.class文件
2. **ASM 字节码操作** - 修改方法的字节码
3. **自动注入** - 在关键方法前后插入性能监控代码

### 监控的方法

| 类名 | 方法名 | 用途 |
|------|--------|------|
| WebView | loadUrl() | 开始加载URL |
| WebViewClient | onPageStarted() | 页面开始加载 |
| WebViewClient | onPageFinished() | 页面加载完成 |
| WebViewClient | onRenderProcessGone() | 渲染进程崩溃 |

### 插桩代码示例

```java
// 原始代码
public void loadUrl(String url) {
    // WebView实现
}

// 插桩后
public void loadUrl(String url) {
    WebViewMonitor.onMethodEnter("loadUrl", url);
    try {
        // WebView实现
    } finally {
        WebViewMonitor.onMethodExit("loadUrl", url);
    }
}
```

---

## 📱 示例应用

项目包含一个完整的示例应用，位于 `app/` 目录：

1. 输入URL
2. 点击"加载"按钮
3. 自动显示性能监控窗口
4. 查看详细的性能数据

运行示例：

```bash
cd android-webview-perf-plugin
./gradlew :app:installDebug
```

---

## ⚠️ 注意事项

1. **最小SDK** - Android 5.0 (API 21)
2. **混淆** - 需要在 `proguard-rules.pro` 中添加监控库的混淆规则
3. **性能影响** - 插桩会增加极小的性能开销（< 5%）
4. **权限** - 需要网络权限（INTERNET）
5. **兼容性** - 支持所有标准WebView实现

---

## 🔐 ProGuard规则

在 `proguard-rules.pro` 中添加：

```proguard
# 监控库混淆规则
-keep class com.webview.perf.monitor.** { *; }
-keepclassmembers class com.webview.perf.monitor.** { *; }
-dontwarn com.webview.perf.monitor.**
```

---

## 📦 构建和发布

### 发布插件到本地Maven仓库

```bash
cd plugin
./gradlew publishToMavenLocal
```

### 在其他项目中使用

```kotlin
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.webview.perf:plugin:1.0.0")
    }
}

apply(plugin = "com.webview.perf.plugin")
```

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

## 📄 许可证

MIT License

---

## 📞 联系方式

如有问题，请提交 Issue 或联系开发者。

---

**享受你的WebView性能监控之旅！** 🚀
