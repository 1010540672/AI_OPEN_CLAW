package com.webview.perf.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.webview.perf.monitor.MemoryMonitor
import com.webview.perf.monitor.WebViewMonitor

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlInput: EditText
    private lateinit var loadButton: Button
    private lateinit var showPerfButton: Button
    private lateinit var showDetailButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化性能监控器
        WebViewMonitor.init(this)
        WebViewMonitor.setEnabled(true)
        WebViewMonitor.setVisualizationEnabled(true)

        // 初始化内存监控器
        MemoryMonitor.init(this)
        MemoryMonitor.setSampleInterval(500)  // 500ms 采样一次

        initViews()
        setupWebView()
    }

    private fun initViews() {
        webView = findViewById(R.id.webview)
        urlInput = findViewById(R.id.url_input)
        loadButton = findViewById(R.id.load_button)
        showPerfButton = findViewById(R.id.show_perf_button)

        // 设置默认URL
        urlInput.setText("https://www.baidu.com")

        loadButton.setOnClickListener {
            val url = urlInput.text.toString()
            if (url.isNotBlank()) {
                loadUrl(url)
            } else {
                Toast.makeText(this, "请输入URL", Toast.LENGTH_SHORT).show()
            }
        }

        showPerfButton.setOnClickListener {
            showPerformanceData()
        }

        // 新增：查看详细性能数据
        showDetailButton = findViewById(R.id.show_detail_button)
        showDetailButton.setOnClickListener {
            val intent = Intent(this, PerformanceDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    // 开始页面加载时，启动内存监控
                    MemoryMonitor.startMonitoring()
                    showToast("开始加载: ${url?.take(30)}...")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // 页面加载完成后，停止内存监控
                    MemoryMonitor.stopMonitoring()
                    showToast("加载完成: ${url?.take(30)}...")
                }

                override fun onRenderProcessGone(view: WebView?, detail: android.webkit.RenderProcessGoneDetail?): Boolean {
                    showToast("渲染进程崩溃")
                    return super.onRenderProcessGone(view, detail)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    // 可以在此更新进度条
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    // 可以在此更新标题
                }
            }
        }
    }

    private fun loadUrl(url: String) {
        // 确保URL有协议前缀
        val finalUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }

        webView.loadUrl(finalUrl)
    }

    private fun showPerformanceData() {
        val perfData = WebViewMonitor.getAllPerfData()
        if (perfData.isEmpty()) {
            Toast.makeText(this, "暂无性能数据", Toast.LENGTH_SHORT).show()
            return
        }

        // 显示最新的一条数据
        perfData.lastOrNull()?.let { lastData ->
            val message = buildString {
                append("URL: ${lastData.url.take(40)}\n")
                append("总耗时: ${lastData.totalDuration}ms\n")
                append("性能评分: ${lastData.getPerformanceScore()}/100\n")
                append("网络: ${lastData.getTotalNetworkTime()}ms\n")
                append("DOM: ${lastData.getTotalDomTime()}ms\n")
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        webView.destroy()
        MemoryMonitor.stopMonitoring()
        super.onDestroy()
    }
}
