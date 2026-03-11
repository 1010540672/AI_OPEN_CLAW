# 常见问题 FAQ

## Q1: 插件是否会影响应用性能？

**A:** 影响非常小（< 5%）。插桩只在关键方法前后添加时间记录，不执行复杂的计算。如果性能特别敏感，可以禁用可视化窗口：

```kotlin
WebViewMonitor.setVisualizationEnabled(false)
```

---

## Q2: 可以只监控特定的WebView吗？

**A:** 可以。你可以控制是否启用监控：

```kotlin
// 只在需要时启用
WebViewMonitor.setEnabled(false)  // 全局禁用

// 在特定的WebView加载前启用
WebViewMonitor.setEnabled(true)
webView.loadUrl("https://example.com")

// 加载完成后禁用
webView.webViewClient = object : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        WebViewMonitor.setEnabled(false)
    }
}
```

---

## Q3: 如何导出性能数据用于分析？

**A:** 有两种方式：

**方式1: 使用可视化窗口的导出功能**
- 点击"导出数据"按钮
- 数据保存为JSON文件到应用私有目录

**方式2: 程序化获取数据**

```kotlin
val allData = WebViewMonitor.getAllPerfData()

// 转换为JSON
val json = kotlinx.serialization.json.Json.encodeToString(
    kotlinx.serialization.serializer<List<WebViewPerfData>>(),
    allData
)

// 保存到文件或上传到服务器
```

---

## Q4: 支持哪些Android版本？

**A:** 最低支持 Android 5.0 (API 21)，建议使用 Android 7.0+ 以获得更好的性能。

---

## Q5: 插桩会修改我的代码吗？

**A:** 不会。插桩是在编译时修改字节码（.class文件），你的源代码完全不变。如果需要移除插件，只需从 `build.gradle.kts` 中删除相关配置即可。

---

## Q6: 如何在Release版本中使用？

**A:** 插件支持Release版本，只需确保混淆规则配置正确：

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"  // 确保包含监控库的混淆规则
            )
        }
    }
}
```

---

## Q7: 可视化窗口可以自定义样式吗？

**A:** 可以修改 `WebViewPerfVisualization.kt` 文件来自定义：

```kotlin
// 修改窗口位置
window?.setGravity(Gravity.BOTTOM or Gravity.END)

// 修改颜色
setBackgroundColor(Color.parseColor("#YourColor"))

// 修改字体大小
textSize = 16f
```

---

## Q8: 如何监控多个WebView？

**A:** 插件会自动监控应用中所有的WebView实例。每个加载会话都有独立的 `sessionId`，可以通过URL区分：

```kotlin
val dataByUrl = WebViewMonitor.getAllPerfData()
    .filter { it.url.contains("example.com") }
```

---

## Q9: 数据会占用多少内存？

**A:** 每条性能数据约占用 1-2KB 内存。建议定期清理：

```kotlin
// 自动清理超过100条的数据
val allData = WebViewMonitor.getAllPerfData()
if (allData.size > 100) {
    // 保留最近50条
    // 或者调用
    WebViewMonitor.clearPerfData()
}
```

---

## Q10: 插件是否支持Multi-DEX？

**A:** 支持。插件在 Transform 阶段处理，与DEX化无关。

---

## Q11: 如何调试插桩过程？

**A:** 在 `WebViewPerfTransform` 中添加日志：

```java
private byte[] instrumentClass(File classFile) throws IOException {
    android.util.Log.d("WebViewPerf", "Processing: " + classFile.getName());

    try (FileInputStream fis = new FileInputStream(classFile)) {
        // ... 插桩逻辑
    } catch (Exception e) {
        android.util.Log.e("WebViewPerf", "Instrumentation failed", e);
    }
}
```

---

## Q12: 支持其他WebView实现吗？

**A:** 理论上支持所有标准WebView实现（Chrome WebView、X5等），只要是继承自 `android.webkit.WebView` 的类。

---

## Q13: 可以监控Hybrid应用吗？

**A:** 可以。插件监控的是WebView层，无论加载的是原生页面还是Hybrid页面，都能正常监控。

---

## Q14: 性能评分的依据是什么？

**A:** 评分基于总加载时间：

| 耗时范围 | 评分 | 等级 |
|----------|------|------|
| < 1s | 90-100 | 优秀 |
| 1-2s | 75-89 | 良好 |
| 2-3s | 60-74 | 一般 |
| 3-5s | 40-59 | 较差 |
| > 5s | 0-39 | 极差 |

你可以修改 `WebViewPerfData.kt` 中的评分逻辑来自定义。

---

## Q15: 如何联系技术支持？

**A:** 请提交 GitHub Issue 或联系开发者。

---

需要更多帮助？欢迎提问！
