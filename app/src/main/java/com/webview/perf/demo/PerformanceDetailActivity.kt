package com.webview.perf.demo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.webview.perf.monitor.MemoryMonitor
import com.webview.perf.monitor.MemorySnapshot
import com.webview.perf.monitor.WebViewMonitor
import com.webview.perf.monitor.WebViewPerfData
import java.text.SimpleDateFormat
import java.util.*

/**
 * 性能数据详情页面
 * 使用 MPAndroidChart 图表库展示 WebView 加载性能数据
 */
class PerformanceDetailActivity : AppCompatActivity() {

    private lateinit var tvUrl: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvMemoryUsage: TextView

    private lateinit var barChartLoadingTime: BarChart
    private lateinit var pieChartStageDistribution: com.github.mikephil.charting.charts.PieChart
    private lateinit var lineChartMemoryTrend: LineChart
    private lateinit var barChartMemoryBreakdown: BarChart

    private lateinit var btnRefresh: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_detail)

        initViews()
        loadPerformanceData()
    }

    private fun initViews() {
        // 头部信息
        tvUrl = findViewById(R.id.tv_url)
        tvTotalTime = findViewById(R.id.tv_total_time)
        tvScore = findViewById(R.id.tv_score)
        tvMemoryUsage = findViewById(R.id.tv_memory_usage)

        // 图表
        barChartLoadingTime = findViewById(R.id.bar_chart_loading_time)
        pieChartStageDistribution = findViewById(R.id.pie_chart_stage_distribution)
        lineChartMemoryTrend = findViewById(R.id.line_chart_memory_trend)
        barChartMemoryBreakdown = findViewById(R.id.bar_chart_memory_breakdown)

        // 按钮
        btnRefresh = findViewById(R.id.btn_refresh)
        btnClear = findViewById(R.id.btn_clear)

        btnRefresh.setOnClickListener {
            loadPerformanceData()
        }

        btnClear.setOnClickListener {
            WebViewMonitor.clearPerfData()
            MemoryMonitor.clearHistory()
            loadPerformanceData()
        }
    }

    private fun loadPerformanceData() {
        val perfDataList = WebViewMonitor.getAllPerfData()
        val memoryHistory = MemoryMonitor.getMemoryHistory()

        if (perfDataList.isNotEmpty()) {
            // 显示最新的性能数据
            val latestData = perfDataList.last()
            displaySummary(latestData, memoryHistory)
            displayLoadingTimeChart(latestData)
            displayStageDistributionChart(latestData)
        }

        if (memoryHistory.isNotEmpty()) {
            displayMemoryTrendChart(memoryHistory)
            displayMemoryBreakdownChart(memoryHistory.last())
        }
    }

    /**
     * 显示摘要信息
     */
    private fun displaySummary(data: WebViewPerfData, memoryHistory: List<MemorySnapshot>) {
        tvUrl.text = "URL: ${data.url.take(50)}${if (data.url.length > 50) "..." else ""}"
        tvTotalTime.text = "总加载时间: ${data.totalDuration}ms"
        tvScore.text = "性能评分: ${data.getPerformanceScore()}/100 (${data.getPerformanceLevel().label})"

        // 显示内存使用情况
        val latestMemory = memoryHistory.lastOrNull()
        if (latestMemory != null) {
            val pssMB = latestMemory.pssMemory / 1024
            tvMemoryUsage.text = "内存占用: ${pssMB}MB (PSS)"
        } else {
            tvMemoryUsage.text = "内存占用: 未采集"
        }

        // 根据性能评分设置颜色
        val scoreColor = when (data.getPerformanceScore()) {
            in 90..100 -> Color.parseColor("#4CAF50")  // 绿色
            in 75..89 -> Color.parseColor("#8BC34A")   // 浅绿
            in 60..74 -> Color.parseColor("#FFC107")   // 黄色
            in 40..59 -> Color.parseColor("#FF9800")   // 橙色
            else -> Color.parseColor("#F44336")        // 红色
        }
        tvScore.setTextColor(scoreColor)
    }

    /**
     * 显示加载时间柱状图
     */
    private fun displayLoadingTimeChart(data: WebViewPerfData) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        // 网络阶段
        data.dnsLookupTime?.let {
            entries.add(BarEntry(0f, it.toFloat()))
            labels.add("DNS")
        }
        data.tcpConnectTime?.let {
            entries.add(BarEntry(1f, it.toFloat()))
            labels.add("TCP")
        }
        data.tlsHandshakeTime?.let {
            entries.add(BarEntry(2f, it.toFloat()))
            labels.add("TLS")
        }
        data.ttfbTime?.let {
            entries.add(BarEntry(3f, it.toFloat()))
            labels.add("TTFB")
        }
        data.downloadTime?.let {
            entries.add(BarEntry(4f, it.toFloat()))
            labels.add("下载")
        }

        // DOM阶段
        data.domParsingTime?.let {
            entries.add(BarEntry(5f, it.toFloat()))
            labels.add("DOM解析")
        }
        data.resourceLoadingTime?.let {
            entries.add(BarEntry(6f, it.toFloat()))
            labels.add("资源加载")
        }
        entries.add(BarEntry(7f, data.renderTime.toFloat()))
        labels.add("渲染")

        val dataSet = BarDataSet(entries, "各阶段耗时 (ms)").apply {
            colors = listOf(
                Color.parseColor("#2196F3"),  // DNS - 蓝色
                Color.parseColor("#2196F3"),  // TCP - 蓝色
                Color.parseColor("#2196F3"),  // TLS - 蓝色
                Color.parseColor("#03A9F4"),  // TTFB - 浅蓝
                Color.parseColor("#00BCD4"),  // 下载 - 青色
                Color.parseColor("#FF9800"),  // DOM解析 - 橙色
                Color.parseColor("#9C27B0"),  // 资源加载 - 紫色
                Color.parseColor("#4CAF50")   // 渲染 - 绿色
            )
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}ms"
                }
            }
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChartLoadingTime.apply {
            this.data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = 45f
            }

            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}ms"
                    }
                }
            }

            axisRight.isEnabled = false
            invalidate()
        }
    }

    /**
     * 显示阶段分布饼图
     */
    private fun displayStageDistributionChart(data: WebViewPerfData) {
        val entries = mutableListOf<PieEntry>()

        val networkTime = data.getTotalNetworkTime()
        val domParsingTime = data.domParsingTime ?: 0
        val resourceTime = data.resourceLoadingTime ?: 0
        val renderTime = data.renderTime

        if (networkTime > 0) {
            entries.add(PieEntry(networkTime.toFloat(), "网络阶段"))
        }
        if (domParsingTime > 0) {
            entries.add(PieEntry(domParsingTime.toFloat(), "DOM解析"))
        }
        if (resourceTime > 0) {
            entries.add(PieEntry(resourceTime.toFloat(), "资源加载"))
        }
        if (renderTime > 0) {
            entries.add(PieEntry(renderTime.toFloat(), "渲染"))
        }

        val dataSet = PieDataSet(entries, "阶段分布").apply {
            colors = listOf(
                Color.parseColor("#2196F3"),  // 网络阶段 - 蓝色
                Color.parseColor("#FF9800"),  // DOM解析 - 橙色
                Color.parseColor("#9C27B0"),  // 资源加载 - 紫色
                Color.parseColor("#4CAF50")   // 渲染 - 绿色
            )
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }

        val pieData = PieData(dataSet)

        pieChartStageDistribution.apply {
            this.data = pieData
            description.isEnabled = false
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            legend.apply {
                isEnabled = true
                orientation = Legend.LegendOrientation.VERTICAL
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            }
            invalidate()
        }
    }

    /**
     * 显示内存趋势折线图
     */
    private fun displayMemoryTrendChart(history: List<MemorySnapshot>) {
        val heapEntries = mutableListOf<Entry>()
        val pssEntries = mutableListOf<Entry>()

        val startTime = history.firstOrNull()?.timestamp ?: 0

        history.forEachIndexed { index, snapshot ->
            val timeOffset = (snapshot.timestamp - startTime) / 1000f  // 秒
            heapEntries.add(Entry(timeOffset, snapshot.javaHeapUsed.toFloat()))
            pssEntries.add(Entry(timeOffset, (snapshot.pssMemory / 1024).toFloat()))
        }

        val heapDataSet = LineDataSet(heapEntries, "Java Heap (MB)").apply {
            color = Color.parseColor("#2196F3")
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val pssDataSet = LineDataSet(pssEntries, "PSS (MB)").apply {
            color = Color.parseColor("#4CAF50")
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(heapDataSet, pssDataSet)

        lineChartMemoryTrend.apply {
            this.data = lineData
            description.isEnabled = false
            legend.isEnabled = true

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}s"
                    }
                }
            }

            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}MB"
                    }
                }
            }

            axisRight.isEnabled = false
            invalidate()
        }
    }

    /**
     * 显示内存分解柱状图
     */
    private fun displayMemoryBreakdownChart(snapshot: MemorySnapshot) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf("Java Heap", "PSS", "Native", "Dalvik", "Other")

        entries.add(BarEntry(0f, snapshot.javaHeapUsed.toFloat()))
        entries.add(BarEntry(1f, snapshot.pssMemory / 1024f))
        entries.add(BarEntry(2f, snapshot.nativeHeapAlloc.toFloat()))
        entries.add(BarEntry(3f, snapshot.dalvikPrivateDirty.toFloat()))
        entries.add(BarEntry(4f, snapshot.otherPrivateDirty.toFloat()))

        val dataSet = BarDataSet(entries, "内存分解 (MB)").apply {
            colors = listOf(
                Color.parseColor("#2196F3"),
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#607D8B")
            )
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}MB"
                }
            }
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChartMemoryBreakdown.apply {
            this.data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labels)
            }

            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}MB"
                    }
                }
            }

            axisRight.isEnabled = false
            invalidate()
        }
    }
}
