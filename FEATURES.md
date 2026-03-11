# Android WebView 性能监控插件 - 功能说明

## 核心功能

### 1. WebView 全生命周期监控

基于业界标准的 WebView 加载生命周期，监控以下阶段：

#### 网络阶段
- **DNS Lookup**: DNS 查询耗时
- **TCP Connect**: TCP 三次握手耗时
- **TLS Handshake**: TLS 握手耗时（HTTPS）
- **TTFB**: Time to First Byte，首字节时间
- **Download**: 内容下载耗时

#### DOM 阶段
- **DOM Parsing**: DOM 树解析耗时
- **Resource Loading**: 资源加载耗时（CSS/JS/图片）
- **Render**: 页面渲染耗时

### 2. 内存监控

实时监控应用内存使用情况：

- **Java Heap**: Java 堆内存使用量
- **PSS**: 进程实际占用内存
- **Native Heap**: Native 堆内存
- **Dalvik Memory**: Dalvik 私有/共享内存
- **System Memory**: 系统内存使用情况

采样频率可配置（默认 500ms），支持内存趋势分析。

### 3. 可视化图表

使用 MPAndroidChart 图表库展示：

- **柱状图**: 各阶段加载时长对比
- **饼图**: 阶段时间分布占比
- **折线图**: 内存使用趋势
- **分解图**: 内存占用详细分解

### 4. 性能评分

根据加载时间自动计算性能评分（0-100）：

| 评分范围 | 等级 | 颜色 |
|----------|------|------|
| 90-100 | 优秀 | 绿色 |
| 75-89 | 良好 | 浅绿 |
| 60-74 | 一般 | 黄色 |
| 40-59 | 较差 | 橙色 |
| 0-39 | 极差 | 红色 |

## 技术实现

### 字节码插桩

使用 Gradle Transform API（或 ASM）在编译期插入监控代码：

```java
// 插桩示例
class WebViewPerformanceVisitor extends MethodVisitor {
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (owner.equals("android/webkit/WebView") && name.equals("loadUrl")) {
            // 插入监控代码
            mv.visitMethodInsn(INVOKESTATIC, 
                "com/webview/perf/monitor/WebViewMonitor", 
                "onLoadUrl", 
                "()V");
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
}
```

### 监控流程

```
1. WebView.loadUrl() 调用
   ↓
2. 记录开始时间，创建 Session
   ↓
3. WebViewClient.onPageStarted()
   ↓
4. 记录页面开始时间
   ↓
5. WebViewClient.onPageFinished()
   ↓
6. 计算各阶段耗时，生成报告
   ↓
7. 显示可视化窗口/更新图表
```

### 内存采样

```kotlin
// 内存采样流程
fun startMonitoring() {
    handler.postDelayed({
        val snapshot = getCurrentMemorySnapshot()
        memoryHistory.add(snapshot)
        scheduleMemoryCheck()
    }, sampleIntervalMs)
}
```

## 使用场景

1. **性能优化**: 定位 WebView 加载慢的根本原因
2. **内存分析**: 检测内存泄漏和异常增长
3. **竞品对比**: 对比不同页面的加载性能
4. **线上监控**: 收集真实用户性能数据
5. **回归测试**: 确保优化效果持续有效

## 数据导出

支持导出 JSON 格式的性能数据：

```json
{
  "sessionId": "1710134400000_https://example.com",
  "url": "https://example.com",
  "totalDuration": 1523,
  "performanceScore": 75,
  "networkTime": {
    "dns": 45,
    "tcp": 120,
    "tls": 89,
    "ttfb": 234,
    "download": 567
  },
  "domTime": {
    "parsing": 234,
    "resourceLoading": 156,
