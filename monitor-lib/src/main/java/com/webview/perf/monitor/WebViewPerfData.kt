package com.webview.perf.monitor

/**
 * WebView性能数据模型
 */
data class WebViewPerfData(
    val sessionId: String,
    val url: String,

    // 时间戳
    var loadStartTime: Long = 0,
    var pageStartedTime: Long = 0,
    var pageFinishedTime: Long = 0,

    // 总体耗时
    var totalDuration: Long = 0,
    var domCompleteTime: Long = 0,

    // 网络阶段耗时
    var dnsLookupTime: Long? = null,
    var tcpConnectTime: Long? = null,
    var tlsHandshakeTime: Long? = null,
    var ttfbTime: Long? = null,        // Time to First Byte
    var downloadTime: Long? = null,

    // DOM阶段耗时
    var domParsingTime: Long? = null,
    var resourceLoadingTime: Long? = null,
    var renderTime: Long = 0,

    // 额外信息
    var httpStatusCode: Int? = null,
    var responseSize: Long? = null,
    var resourceCount: Int = 0,
    var jsExecutionTime: Long? = null,
    var layoutTime: Long? = null,
    var paintTime: Long? = null
) {
    /**
     * 获取网络总耗时
     */
    fun getTotalNetworkTime(): Long {
        return (dnsLookupTime ?: 0) +
               (tcpConnectTime ?: 0) +
               (tlsHandshakeTime ?: 0) +
               (ttfbTime ?: 0) +
               (downloadTime ?: 0)
    }

    /**
     * 获取DOM总耗时
     */
    fun getTotalDomTime(): Long {
        return (domParsingTime ?: 0) +
               (resourceLoadingTime ?: 0) +
               renderTime
    }

    /**
     * 获取性能评分（0-100）
     */
    fun getPerformanceScore(): Int {
        val score = when {
            totalDuration < 1000 -> 100
            totalDuration < 2000 -> 90
            totalDuration < 3000 -> 75
            totalDuration < 5000 -> 60
            totalDuration < 8000 -> 40
            else -> 20
        }
        return score
    }

    /**
     * 获取性能等级
     */
    fun getPerformanceLevel(): PerformanceLevel {
        return when (getPerformanceScore()) {
            in 90..100 -> PerformanceLevel.EXCELLENT
            in 75..89 -> PerformanceLevel.GOOD
            in 60..74 -> PerformanceLevel.FAIR
            in 40..59 -> PerformanceLevel.POOR
            else -> PerformanceLevel.CRITICAL
        }
    }

    fun toDetailedString(): String {
        return buildString {
            appendLine("WebView Performance Report")
            appendLine("=" .repeat(50))
            appendLine("URL: $url")
            appendLine("Session ID: $sessionId")
            appendLine()
            appendLine("【总体指标】")
            appendLine("  总加载时间: ${totalDuration}ms")
            appendLine("  性能评分: ${getPerformanceScore()}/100 (${getPerformanceLevel().label})")
            appendLine()
            appendLine("【网络阶段】")
            appendLine("  DNS查询: ${dnsLookupTime ?: "N/A"}ms")
            appendLine("  TCP连接: ${tcpConnectTime ?: "N/A"}ms")
            appendLine("  TLS握手: ${tlsHandshakeTime ?: "N/A"}ms")
            appendLine("  TTFB: ${ttfbTime ?: "N/A"}ms")
            appendLine("  内容下载: ${downloadTime ?: "N/A"}ms")
            appendLine("  网络总计: ${getTotalNetworkTime()}ms")
            appendLine()
            appendLine("【DOM阶段】")
            appendLine("  DOM解析: ${domParsingTime ?: "N/A"}ms")
            appendLine("  资源加载: ${resourceLoadingTime ?: "N/A"}ms")
            appendLine("  渲染: ${renderTime}ms")
            appendLine("  DOM总计: ${getTotalDomTime()}ms")
            appendLine()
            appendLine("【额外信息】")
            appendLine("  HTTP状态: ${httpStatusCode ?: "N/A"}")
            appendLine("  响应大小: ${responseSize ?: "N/A"} bytes")
            appendLine("  资源数量: $resourceCount")
            appendLine("  JS执行: ${jsExecutionTime ?: "N/A"}ms")
            appendLine("  布局: ${layoutTime ?: "N/A"}ms")
            appendLine("  绘制: ${paintTime ?: "N/A"}ms")
        }
    }
}

enum class PerformanceLevel(val label: String, val color: String) {
    EXCELLENT("优秀", "#4CAF50"),
    GOOD("良好", "#8BC34A"),
    FAIR("一般", "#FFC107"),
    POOR("较差", "#FF9800"),
    CRITICAL("极差", "#F44336")
}
