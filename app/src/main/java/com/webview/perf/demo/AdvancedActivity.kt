package com.webview.perf.demo

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.webview.perf.monitor.WebViewMonitor
import com.webview.perf.monitor.WebViewPerfData
import com.webview.perf.monitor.NetworkStage

/**
 * 高级使用示例
 */
class AdvancedActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced)

        // 1. 初始化监控器
        WebViewMonitor.init(this)
        WebViewMonitor.setEnabled(true)
        WebViewMonitor.setVisualizationEnabled(true)

        // 2. 配置WebView
        setupWebView()

        // 3. 加载URL
        webView.loadUrl("https://github.com")
    }

    private fun setupWebView() {
        webView = findViewById(R.id.webview)
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            }

            // 自定义WebViewClient进行高级监控
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    // 可以在这里添加自定义逻辑
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    // 4. 获取性能数据
                    val perfData = WebViewMonitor.getAllPerfData()
                    perfData.lastOrNull()?.let { data ->
                        displayPerfSummary(data)
                    }
                }
            }
        }
    }

    private fun displayPerfSummary(data: WebViewPerfData) {
        val summary = """
            URL: ${data.url.take(40)}
            总耗时: ${data.totalDuration}ms
            性能评分: ${data.getPerformanceScore()}/100
            等级: ${data.getPerformanceLevel().label}
            ──────────────────
            网络: ${data.getTotalNetworkTime()}ms
            DOM: ${data.getTotalDomTime()}ms
        """.trimIndent()

        Toast.makeText(this, summary, Toast.LENGTH_LONG).show()
    }
}
