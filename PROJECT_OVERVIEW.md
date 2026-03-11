# 项目文件清单

## 核心组件

### 1. 插桩插件 (plugin/)
字节码插桩的核心实现。

| 文件 | 说明 | 关键类 |
|------|------|--------|
| `WebViewPerfPlugin.java` | Gradle插件入口 | `implements Plugin<Project>` |
| `WebViewPerfTransform.java` | Transform实现，处理字节码转换 | `extends Transform` |
| `WebViewPerformanceVisitor.java` | ASM访问器，注入监控代码 | `extends ClassVisitor` |
| `build.gradle.kts` | 插件构建配置 | Gradle DSL |

### 2. 监控库 (monitor-lib/)
性能监控的核心功能实现。

| 文件 | 说明 | 主要功能 |
|------|------|----------|
| `WebViewMonitor.kt` | 监控器核心类 | 数据收集、回调处理 |
| `WebViewPerfData.kt` | 性能数据模型 | 数据结构、评分算法 |
| `WebViewPerfVisualization.kt` | 可视化窗口 | 浮动窗口、图表显示 |
| `build.gradle.kts` | 库构建配置 | Android Library |

### 3. 示例应用 (app/)
完整的使用示例。

| 文件 | 说明 |
|------|------|
| `MainActivity.kt` | 基础使用示例 |
| `AdvancedActivity.kt` | 高级使用示例 |
| `activity_main.xml` | 主界面布局 |
| `AndroidManifest.xml` | 应用清单 |
| `proguard-rules.pro` | 混淆规则 |
| `build.gradle.kts` | 应用构建配置 |

### 4. 项目配置
根目录配置文件。

| 文件 | 说明 |
|------|------|
| `build.gradle.kts` | 项目级构建配置 |
| `settings.gradle.kts` | 模块配置 |
| `gradle.properties` | Gradle属性 |

### 5. 文档
详细的使用文档。

| 文件 | 说明 |
|------|------|
| `README.md` | 完整使用手册 |
| `INTEGRATION.md` | 快速集成指南 |
| `FAQ.md` | 常见问题解答 |

---

## 技术栈

### 字节码操作
- **ASM 9.6** - Java字节码操作框架
- **Gradle Transform API** - Android构建转换

### Android
- **Kotlin 1.9.10** - 主要开发语言
- **Android Gradle Plugin 8.2.0** - 构建工具
- **minSdk 21** - 最低支持Android 5.0
- **targetSdk 34** - 目标Android 14

### 依赖库
- `androidx.core:core-ktx` - Android核心库
- `androidx.webkit:webkit` - WebView支持
- `kotlinx-serialization-json` - JSON序列化

---

## 工作流程

```
1. 编译阶段
   └─> Gradle Transform
       └─> WebViewPerfTransform
           └─> 扫描.class文件
               └─> WebViewPerformanceVisitor
                   └─> ASM字节码注入
                       └─> 生成修改后的.class

2. 运行时
   └─> WebView.loadUrl()
       └─> 触发插桩代码
           └─> WebViewMonitor.onMethodEnter()
               └─> 记录开始时间
           └─> WebViewClient.onPageFinished()
               └─> WebViewMonitor.onMethodExit()
                   └─> 计算总耗时
                       └─> WebViewPerfVisualization.show()
                           └─> 显示浮动窗口

3. 数据收集
   └─> WebViewPerfData
       └─> 存储所有性能指标
           └─> 计算性能评分
               └─> 导出JSON报告
```

---

## 监控指标详解

### 网络阶段
```
loadUrl()
  ↓
DNS Lookup ────────────> [记录dnsLookupTime]
  ↓
TCP Connect ────────────> [记录tcpConnectTime]
  ↓
TLS Handshake ───────────> [记录tlsHandshakeTime]
  ↓
TTFB ────────────────> [记录ttfbTime]
  ↓
Download ────────────> [记录downloadTime]
```

### DOM阶段
```
onPageStarted()
  ↓
DOM Parsing ────────────> [记录domParsingTime]
  ↓
Resource Loading ───────> [记录resourceLoadingTime]
  ↓
Rendering ────────────> [记录renderTime]
  ↓
onPageFinished()
```

---

## 性能影响评估

| 指标 | 影响程度 | 说明 |
|------|----------|------|
| APK大小 | +15KB | 监控库的体积 |
| 运行内存 | +2KB/次 | 每次加载的数据 |
| CPU | +1-3% | 时间记录的计算 |
| 加载时间 | +5-10ms | 插桩代码的执行 |

---

## 扩展性

### 添加新的监控指标

在 `WebViewPerfData.kt` 中添加新字段：

```kotlin
data class WebViewPerfData(
    // 现有字段...
    var yourCustomMetric: Long? = null
)
```

在 `WebViewMonitor.kt` 中添加更新方法：

```kotlin
fun updateCustomMetric(sessionId: String, value: Long) {
    perfDataList[sessionId]?.yourCustomMetric = value
}
```

### 自定义可视化

修改 `WebViewPerfVisualization.kt` 中的UI代码：

```kotlin
private fun createCustomView(data: WebViewPerfData): View {
    // 自定义你的可视化组件
}
```

### 自定义评分算法

修改 `WebViewPerfData.kt` 中的评分逻辑：

```kotlin
fun getPerformanceScore(): Int {
    // 实现你自己的评分算法
    return when {
        yourCondition1 -> 100
        yourCondition2 -> 80
        else -> 60
    }
}
```

---

## 安全性

| 方面 | 说明 |
|------|------|
| 代码混淆 | 支持，需配置ProGuard规则 |
| 数据安全 | 数据存储在内存中，应用关闭即清空 |
| 网络安全 | 不进行任何网络传输 |
| 权限需求 | 仅需INTERNET权限（WebView需要） |

---

## 已知限制

1. 无法监控跨域Iframe的性能
2. 网络阶段的详细耗时需要从WebView的其他回调手动更新
3. 不支持Android 4.4及以下版本
4. 可视化窗口在某些设备上可能被系统限制显示

---

## 未来规划

- [ ] 支持导出Har格式（HTTP Archive）
- [ ] 添加性能趋势图表
- [ ] 支持远程监控和数据上报
- [ ] 添加性能优化建议
- [ ] 支持多WebView对比分析

---

## 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 许可证

MIT License - 详见 LICENSE 文件
