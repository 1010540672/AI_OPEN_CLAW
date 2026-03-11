package com.webview.perf.monitor

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * WebView性能监控核心类
 * 监控URL加载全生命周期耗时
 */
object WebViewMonitor {

    private val startTimeMap = ConcurrentHashMap<String, Long>()
    private val perfDataList = ConcurrentHashMap<String, WebViewPerfData>()

    private var context: Context? = null
    private var enabled = true
    private var visualizationEnabled = true

    private val uiHandler = Handler(Looper.getMainLooper())
    private var perfVisualizationWindow: WebViewPerfVisualization? = null

    /**
     * 初始化监控器
     */
    fun init(context: Context) {
        this.context = context.applicationContext
    }

    /**
     * 启用/禁用监控
     */
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    /**
     * 启用/禁用可视化
     */
    fun setVisualizationEnabled(enabled: Boolean) {
        this.visualizationEnabled = enabled
        if (!enabled) {
            perfVisualizationWindow?.dismiss()
            perfVisualizationWindow = null
        }
    }

    /**
     * 方法进入时的回调
     */
    fun onMethodEnter(methodName: String, obj: Any?) {
        if (!enabled) return

        val currentTime = System.nanoTime()

        when (methodName) {
            "loadUrl", "loadData", "loadDataWithBaseURL" -> {
                // 记录加载URL的开始时间
                val url = when (obj) {
                    is String -> obj
                    is WebView -> obj.url ?: "unknown"
                    else -> "unknown"
                }
                val sessionId = generateSessionId(url)
                startTimeMap["loadUrl_$sessionId"] = currentTime

                perfDataList[sessionId] = WebViewPerfData(
                    sessionId = sessionId,
                    url = url,
                    loadStartTime = System.currentTimeMillis()
                )
            }
        }
    }

    /**
     * 方法退出时的回调
     */
    fun onMethodExit(methodName: String, obj: Any?) {
        if (!enabled) return

        val currentTime = System.nanoTime()
        val durationNs = System.nanoTime() - currentTime // 实际应该是在方法内计算

        when (methodName) {
            "onPageStarted" -> {
                val url = when (obj) {
                    is String -> obj
                    else -> "unknown"
                }
                val sessionId = generateSessionId(url)
                perfDataList[sessionId]?.pageStartedTime = System.currentTimeMillis()
            }

            "onPageFinished" -> {
                val url = when (obj) {
                    is String -> obj
                    else -> "unknown"
                }
                val sessionId = generateSessionId(url)

                perfDataList[sessionId]?.let { data ->
                    data.pageFinishedTime = System.currentTimeMillis()
                    data.totalDuration = data.pageFinishedTime - data.loadStartTime
                    data.domCompleteTime = data.pageFinishedTime - data.pageStartedTime

                    // 计算各个阶段耗时
                    data.dnsLookupTime = data.dnsLookupTime ?: 0
                    data.tcpConnectTime = data.tcpConnectTime ?: 0
                    data.tlsHandshakeTime = data.tlsHandshakeTime ?: 0
                    data.ttfbTime = data.ttfbTime ?: 0
                    data.downloadTime = data.downloadTime ?: 0
                    data.domParsingTime = data.domParsingTime ?: 0
                    data.resourceLoadingTime = data.resourceLoadingTime ?: 0
                    data.renderTime = System.currentTimeMillis() - (data.pageFinishedTime ?: 0)

                    // 显示可视化窗口
                    if (visualizationEnabled) {
                        showVisualization(data)
                    }

                    // 打印性能数据
                    logPerfData(data)
                }
            }
        }
    }

    /**
     * 设置各个阶段的耗时（需要从WebView的其他回调中获取）
     */
    fun updateNetworkTiming(sessionId: String, stage: NetworkStage, durationMs: Long) {
        perfDataList[sessionId]?.let { data ->
            when (stage) {
                NetworkStage.DNS_LOOKUP -> data.dnsLookupTime = durationMs
                NetworkStage.TCP_CONNECT -> data.tcpConnectTime = durationMs
                NetworkStage.TLS_HANDSHAKE -> data.tlsHandshakeTime = durationMs
                NetworkStage.TTFB -> data.ttfbTime = durationMs
                NetworkStage.DOWNLOAD -> data.downloadTime = durationMs
            }
        }
    }

    fun updateDomTiming(sessionId: String, stage: DomStage, durationMs: Long) {
        perfDataList[sessionId]?.let { data ->
            when (stage) {
                DomStage.PARSING -> data.domParsingTime = durationMs
                DomStage.RESOURCE_LOADING -> data.resourceLoadingTime = durationMs
            }
        }
    }

    /**
     * 获取所有性能数据
     */
    fun getAllPerfData(): List<WebViewPerfData> {
        return perfDataList.values.toList().sortedByDescending { it.loadStartTime }
    }

    /**
     * 清空性能数据
     */
    fun clearPerfData() {
        perfDataList.clear()
        startTimeMap.clear()
    }

    /**
     * 获取特定URL的性能数据
     */
    fun getPerfDataByUrl(url: String): WebViewPerfData? {
        return perfDataList.values.find { it.url == url }
    }

    private fun generateSessionId(url: String): String {
        return "${System.currentTimeMillis()}_${url.take(20)}"
    }

    private fun showVisualization(data: WebViewPerfData) {
        uiHandler.post {
            context?.let { ctx ->
                if (perfVisualizationWindow == null) {
                    perfVisualizationWindow = WebViewPerfVisualization(ctx)
                }
                perfVisualizationWindow?.updateData(data)
                perfVisualizationWindow?.show()
            }
        }
    }

    private fun logPerfData(data: WebViewPerfData) {
        android.util.Log.d("WebViewPerf", """
            |
            |========================================
            |WebView Performance Report
            |========================================
            |URL: ${data.url}
            |Session: ${data.sessionId}
            |
            |总体耗时:
            |  总加载时间: ${data.totalDuration}ms
            |  DOM加载完成: ${data.domCompleteTime}ms
            |
            |网络阶段:
            |  DNS查询: ${data.dnsLookupTime}ms
            |  TCP连接: ${data.tcpConnectTime}ms
            |  TLS握手: ${data.tlsHandshakeTime}ms
            |  TTFB: ${data.ttfbTime}ms
            |  内容下载: ${data.downloadTime}ms
            |
            |DOM阶段:
            |  DOM解析: ${data.domParsingTime}ms
            |  资源加载: ${data.resourceLoadingTime}ms
            |  渲染: ${data.renderTime}ms
            |========================================
        """.trimMargin())
    }
}

enum class NetworkStage {
    DNS_LOOKUP, TCP_CONNECT, TLS_HANDSHAKE, TTFB, DOWNLOAD
}

enum class DomStage {
    PARSING, RESOURCE_LOADING
}
