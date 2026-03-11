package com.webview.perf.monitor

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * WebView性能可视化窗口
 * 实时显示性能数据
 */
class WebViewPerfVisualization(context: Context) : Dialog(context) {

    private lateinit var container: LinearLayout
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置窗口属性
        window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.TOP or Gravity.END)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        setupUI()
    }

    private fun setupUI() {
        setContentView(createRootView())
    }

    private fun createRootView(): ScrollView {
        scrollView = ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#E8F5E9"))
            setPadding(16, 16, 16, 16)
        }

        container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        scrollView.addView(container)
        return scrollView
    }

    fun updateData(data: WebViewPerfData) {
        container.removeAllViews()
        container.addView(createHeaderView(data))
        container.addView(createProgressBar(data))
        container.addView(createTimingBreakdown(data))
        container.addView(createActionButtons())
    }

    private fun createHeaderView(data: WebViewPerfData): TextView {
        return TextView(context).apply {
            text = """
                🚀 WebView性能监控
                ════════════════════
                📊 URL: ${data.url.take(30)}${if (data.url.length > 30) "..." else ""}
                ⏱️ 总耗时: ${data.totalDuration}ms
                🎯 评分: ${data.getPerformanceScore()}/100
            """.trimIndent()
            textSize = 14f
            setTextColor(Color.parseColor("#2E7D32"))
            setPadding(8, 8, 8, 8)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }

    private fun createProgressBar(data: WebViewPerfData): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 16, 8, 8)

            addView(createProgressBarItem("网络阶段", data.getTotalNetworkTime(), data.totalDuration, "#2196F3"))
            addView(createProgressBarItem("DOM解析", data.domParsingTime ?: 0, data.totalDuration, "#FF9800"))
            addView(createProgressBarItem("资源加载", data.resourceLoadingTime ?: 0, data.totalDuration, "#9C27B0"))
            addView(createProgressBarItem("渲染", data.renderTime, data.totalDuration, "#4CAF50"))
        }
    }

    private fun createProgressBarItem(label: String, value: Long, total: Long, color: String): LinearLayout {
        val percentage = if (total > 0) (value * 100 / total).toInt() else 0

        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 4, 0, 4) }

            // 标签和数值
            addView(TextView(context).apply {
                text = "$label: ${value}ms ($percentage%)"
                textSize = 12f
                setTextColor(Color.parseColor("#424242"))
            })

            // 进度条
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    20
                )
                setBackgroundColor(Color.parseColor("#E0E0E0"))

                addView(LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        percentage,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.parseColor(color))
                })
            })
        }
    }

    private fun createTimingBreakdown(data: WebViewPerfData): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 16, 8, 8)

            addView(createSectionTitle("网络详细"))

            addView(createTimingRow("DNS查询", data.dnsLookupTime))
            addView(createTimingRow("TCP连接", data.tcpConnectTime))
            addView(createTimingRow("TLS握手", data.tlsHandshakeTime))
            addView(createTimingRow("TTFB", data.ttfbTime))
            addView(createTimingRow("下载", data.downloadTime))

            addView(createSectionTitle("DOM详细"))

            addView(createTimingRow("解析", data.domParsingTime))
            addView(createTimingRow("资源", data.resourceLoadingTime))
            addView(createTimingRow("渲染", data.renderTime))
        }
    }

    private fun createSectionTitle(title: String): TextView {
        return TextView(context).apply {
            text = "▶ $title"
            textSize = 13f
            setTextColor(Color.parseColor("#1976D2"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, 8, 0, 4)
        }
    }

    private fun createTimingRow(label: String, value: Long?): TextView {
        val displayValue = value?.toString() ?: "N/A"
        return TextView(context).apply {
            text = "  • $label: $displayValue ms"
            textSize = 11f
            setTextColor(Color.parseColor("#616161"))
        }
    }

    private fun createActionButtons(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 0) }

            addView(Button(context).apply {
                text = "复制报告"
                setOnClickListener {
                    copyReportToClipboard()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(0, 0, 4, 0) }
            })

            addView(Button(context).apply {
                text = "导出数据"
                setOnClickListener {
                    exportData()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(4, 0, 0, 0) }
            })

            addView(Button(context).apply {
                text = "清空"
                setOnClickListener {
                    WebViewMonitor.clearPerfData()
                    dismiss()
                }
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply { setMargins(4, 0, 0, 0) }
            })
        }
    }

    private fun copyReportToClipboard() {
        val data = WebViewMonitor.getAllPerfData().lastOrNull()
        data?.let {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("WebView Performance Report", it.toDetailedString())
            clipboard.setPrimaryClip(clip)
            android.widget.Toast.makeText(context, "已复制到剪贴板", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportData() {
        val data = WebViewMonitor.getAllPerfData()
        val jsonString = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.serializer<List<WebViewPerfData>>(),
            data
        )

        // 保存到文件
        val filename = "webview_perf_${System.currentTimeMillis()}.json"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }

        val file = context.getFileStreamPath(filename)
        android.widget.Toast.makeText(context, "已导出到: ${file.absolutePath}", android.widget.Toast.LENGTH_LONG).show()
    }
}
