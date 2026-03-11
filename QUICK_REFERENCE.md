# 快速参考

## 核心API速查

### WebViewMonitor

```kotlin
// 初始化
WebViewMonitor.init(context)
WebViewMonitor.setEnabled(true)
WebViewMonitor.setVisualizationEnabled(true)

// 获取数据
val allData = WebViewMonitor.getAllPerfData()
val specificData = WebViewMonitor.getPerfDataByUrl("https://example.com")

// 更新指标
WebViewMonitor.updateNetworkTiming(sessionId, NetworkStage.DNS_LOOKUP, 150)
WebViewMonitor.updateDomTiming(sessionId, DomStage.PARSING, 200)

// 清理数据
WebViewMonitor.clearPerfData()
```

### WebViewPerfData

```kotlin
// 性能评分
val score = data.getPerformanceScore() // 0-100
val level = data.getPerformanceLevel() // EXCELLENT/GOOD/FAIR/POOR/CRITICAL

// 耗时统计
val networkTime = data.getTotalNetworkTime()
val domTime = data.getTotalDomTime()

// 详细信息
val jsonString = data.toDetailedString()
```

## Gradle配置模板

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":monitor-lib"))
}

apply {
    plugin("com.webview.perf.plugin")
}

configure<com.webview.perf.WebViewPerfExtension> {
    enableTrace = true
    enableVisualization = true
    packageName = "com.your.package"
}
```

## Application初始化

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WebViewMonitor.init(this)
        WebViewMonitor.setEnabled(true)
        WebViewMonitor.setVisualizationEnabled(true)
    }
}
```

## WebViewClient集成

```kotlin
webView.webViewClient = object : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        // 可选：更新网络计时
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        // 自动显示性能窗口
    }
}
```

## ProGuard规则

```proguard
-keep class com.webview.perf.monitor.** { *; }
-dontwarn com.webview.perf.monitor.**
```

## 性能等级对照表

| 评分 | 等级 | 颜色 | 加载时间 |
|------|------|------|----------|
| 90-100 | 优秀 | 🟢 | < 1s |
| 75-89 | 良好 | 🟢 | 1-2s |
| 60-74 | 一般 | 🟡 | 2-3s |
| 40-59 | 较差 | 🟠 | 3-5s |
| 0-39 | 极差 | 🔴 | > 5s |

## 文件位置速查

```
plugin/src/main/java/com/webview/perf/
├── WebViewPerfPlugin.java          ← 插件入口
├── WebViewPerfTransform.java       ← Transform实现
└── WebViewPerformanceVisitor.java  ← ASM访问器

monitor-lib/src/main/java/com/webview/perf/monitor/
├── WebViewMonitor.kt                ← 监控核心
├── WebViewPerfData.kt              ← 数据模型
└── WebViewPerfVisualization.kt     ← 可视化
```

## 构建命令

```bash
# 清理构建
./gradlew clean

# 编译Debug
./gradlew assembleDebug

# 安装到设备
./gradlew :app:installDebug

# 发布插件到本地
./gradlew :plugin:publishToMavenLocal
```

## 常用目录

| 类型 | 路径 |
|------|------|
| 插件源码 | `plugin/src/main/java/com/webview/perf/` |
| 监控库源码 | `monitor-lib/src/main/java/com/webview/perf/monitor/` |
| 示例应用 | `app/src/main/java/com/webview/perf/demo/` |
| 混淆规则 | `app/proguard-rules.pro` |
| 导出数据 | `/data/data/<package>/files/webview_perf_*.json` |

## 监控的阶段

### NetworkStage
```kotlin
DNS_LOOKUP      // DNS查询
TCP_CONNECT     // TCP连接
TLS_HANDSHAKE   // TLS握手
TTFB            // 首字节时间
DOWNLOAD        // 内容下载
```

### DomStage
```kotlin
PARSING         // DOM解析
RESOURCE_LOADING // 资源加载
```

## 性能指标

| 指标 | 单位 | 说明 |
|------|------|------|
| loadStartTime | ms | loadUrl调用时间 |
| pageStartedTime | ms | onPageStarted时间 |
| pageFinishedTime | ms | onPageFinished时间 |
| totalDuration | ms | 总加载时间 |
| dnsLookupTime | ms | DNS查询耗时 |
| tcpConnectTime | ms | TCP连接耗时 |
| tlsHandshakeTime | ms | TLS握手耗时 |
| ttfbTime | ms | 首字节耗时 |
| downloadTime | ms | 内容下载耗时 |
| domParsingTime | ms | DOM解析耗时 |
| resourceLoadingTime | ms | 资源加载耗时 |
| renderTime | ms | 渲染耗时 |

## 日志过滤

```bash
# 查看性能日志
adb logcat | grep "WebViewPerf"

# 查看插桩日志
adb logcat | grep "WebViewPerfTransform"

# 查看完整日志
adb logcat -v threadtime
```

## 调试技巧

### 检查是否正常工作

```kotlin
// 加载URL后检查
val data = WebViewMonitor.getAllPerfData()
if (data.isNotEmpty()) {
    val last = data.last()
    Log.d("WebViewPerf", "URL: ${last.url}, 耗时: ${last.totalDuration}ms")
}
```

### 测试可视化窗口

```kotlin
WebViewMonitor.setVisualizationEnabled(true)
// 确保在主线程
runOnUiThread {
    val dummyData = WebViewPerfData(
        sessionId = "test",
        url = "https://test.com",
        loadStartTime = System.currentTimeMillis() - 1000,
        totalDuration = 1000
    )
    // 触发显示
}
```

---

**打印本文件作为桌面参考卡片！** 📋
