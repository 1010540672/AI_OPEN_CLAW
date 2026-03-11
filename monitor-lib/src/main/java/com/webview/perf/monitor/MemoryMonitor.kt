package com.webview.perf.monitor

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.os.Process
import java.io.BufferedReader
import java.io.FileReader

/**
 * 内存监控器
 * 实时监控 WebView 加载过程中的内存使用情况
 */
object MemoryMonitor {

    private var context: Context? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false

    // 内存数据历史记录
    private val memoryHistory = mutableListOf<MemorySnapshot>()

    // 采样间隔（毫秒）
    private var sampleIntervalMs = 500L

    // 最大记录数
    private var maxHistorySize = 200

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    /**
     * 设置采样间隔
     */
    fun setSampleInterval(intervalMs: Long) {
        this.sampleIntervalMs = intervalMs
    }

    /**
     * 开始内存监控
     */
    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        memoryHistory.clear()
        scheduleMemoryCheck()
    }

    /**
     * 停止内存监控
     */
    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 获取内存历史记录
     */
    fun getMemoryHistory(): List<MemorySnapshot> = memoryHistory.toList()

    /**
     * 清空历史记录
     */
    fun clearHistory() {
        memoryHistory.clear()
    }

    /**
     * 获取当前内存快照
     */
    fun getCurrentMemorySnapshot(): MemorySnapshot {
        val runtime = Runtime.getRuntime()
        val currentTime = System.currentTimeMillis()

        // 获取运行时内存
        val maxMemory = runtime.maxMemory()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory

        // 获取系统内存信息
        val systemMemory = getSystemMemoryInfo()

        // 获取进程内存信息（PSS）
        val pssMemory = getProcessPssMemory()

        // 获取堆内存详情
        val heapMemory = getHeapMemoryInfo()

        // 获取 Native 内存
        val nativeMemory = getNativeMemoryInfo()

        return MemorySnapshot(
            timestamp = currentTime,
            javaHeapUsed = usedMemory / 1024 / 1024,  // MB
            javaHeapMax = maxMemory / 1024 / 1024,    // MB
            javaHeapTotal = totalMemory / 1024 / 1024, // MB
            javaHeapFree = freeMemory / 1024 / 1024,   // MB
            pssMemory = pssMemory,
            systemAvailableMemory = systemMemory.available / 1024,  // MB
            systemTotalMemory = systemMemory.total / 1024,          // MB
            nativeHeapSize = nativeMemory.heapSize / 1024 / 1024,   // MB
            nativeHeapAlloc = nativeMemory.allocSpace / 1024 / 1024, // MB
            dalvikPrivateDirty = heapMemory.dalvikPrivateDirty / 1024, // MB
            dalvikSharedDirty = heapMemory.dalvikSharedDirty / 1024,   // MB
            nativePrivateDirty = heapMemory.nativePrivateDirty / 1024, // MB
            otherPrivateDirty = heapMemory.otherPrivateDirty / 1024    // MB
        )
    }

    private fun scheduleMemoryCheck() {
        if (!isMonitoring) return

        handler.postDelayed({
            if (isMonitoring) {
                val snapshot = getCurrentMemorySnapshot()
                addMemorySnapshot(snapshot)
                scheduleMemoryCheck()
            }
        }, sampleIntervalMs)
    }

    private fun addMemorySnapshot(snapshot: MemorySnapshot) {
        memoryHistory.add(snapshot)
        if (memoryHistory.size > maxHistorySize) {
            memoryHistory.removeAt(0)
        }
    }

    /**
     * 获取系统内存信息
     */
    private fun getSystemMemoryInfo(): SystemMemoryInfo {
        val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)

        return SystemMemoryInfo(
            available = memoryInfo?.availMem ?: 0,
            total = memoryInfo?.totalMem ?: 0
        )
    }

    /**
     * 获取进程 PSS 内存
     */
    private fun getProcessPssMemory(): Long {
        return try {
            val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val pids = intArrayOf(Process.myPid())
            val memoryInfo = activityManager?.getProcessMemoryInfo(pids)
            memoryInfo?.getOrNull(0)?.totalPss?.toLong() ?: 0
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 获取堆内存详情
     */
    private fun getHeapMemoryInfo(): HeapMemoryInfo {
        return try {
            val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val pids = intArrayOf(Process.myPid())
            val memoryInfo = activityManager?.getProcessMemoryInfo(pids)

            HeapMemoryInfo(
                dalvikPrivateDirty = memoryInfo?.getOrNull(0)?.dalvikPrivateDirty?.toLong() ?: 0,
                dalvikSharedDirty = memoryInfo?.getOrNull(0)?.dalvikSharedDirty?.toLong() ?: 0,
                nativePrivateDirty = memoryInfo?.getOrNull(0)?.nativePrivateDirty?.toLong() ?: 0,
                otherPrivateDirty = memoryInfo?.getOrNull(0)?.otherPrivateDirty?.toLong() ?: 0
            )
        } catch (e: Exception) {
            HeapMemoryInfo(0, 0, 0, 0)
        }
    }

    /**
     * 获取 Native 内存信息
     */
    private fun getNativeMemoryInfo(): NativeMemoryInfo {
        return try {
            val debugMemoryInfo = Debug.MemoryInfo()
            Debug.getMemoryInfo(debugMemoryInfo)

            NativeMemoryInfo(
                heapSize = Debug.getNativeHeapSize(),
                allocSpace = Debug.getNativeHeapAllocatedSize()
            )
        } catch (e: Exception) {
            NativeMemoryInfo(0, 0)
        }
    }

    /**
     * 获取进程的内存详情（通过 /proc/[pid]/status）
     */
    fun getProcessMemoryStatus(): ProcessMemoryStatus {
        return try {
            val pid = Process.myPid()
            val statusFile = "/proc/$pid/status"
            val reader = BufferedReader(FileReader(statusFile))

            var vmRSS = 0L
            var vmSize = 0L
            var threads = 0

            reader.use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    line?.let {
                        when {
                            it.startsWith("VmRSS:") -> {
                                vmRSS = parseMemoryValue(it)
                            }
                            it.startsWith("VmSize:") -> {
                                vmSize = parseMemoryValue(it)
                            }
                            it.startsWith("Threads:") -> {
                                threads = it.split(":")[1].trim().toIntOrNull() ?: 0
                            }
                        }
                    }
                }
            }

            ProcessMemoryStatus(
                vmRSS = vmRSS,
                vmSize = vmSize,
                threads = threads
            )
        } catch (e: Exception) {
            ProcessMemoryStatus(0, 0, 0)
        }
    }

    private fun parseMemoryValue(line: String): Long {
        return try {
            val parts = line.split(":")[1].trim().split(" ")
            parts[0].toLong()
        } catch (e: Exception) {
            0L
        }
    }
}

/**
 * 内存快照数据类
 */
data class MemorySnapshot(
    val timestamp: Long,
    val javaHeapUsed: Long,          // Java 堆已使用内存 (MB)
    val javaHeapMax: Long,           // Java 堆最大内存 (MB)
    val javaHeapTotal: Long,         // Java 堆总内存 (MB)
    val javaHeapFree: Long,          // Java 堆空闲内存 (MB)
    val pssMemory: Long,             // 进程 PSS 内存 (KB)
    val systemAvailableMemory: Long, // 系统可用内存 (MB)
    val systemTotalMemory: Long,     // 系统总内存 (MB)
    val nativeHeapSize: Long,        // Native 堆大小 (MB)
    val nativeHeapAlloc: Long,       // Native 堆分配内存 (MB)
    val dalvikPrivateDirty: Long,    // Dalvik 私有脏内存 (MB)
    val dalvikSharedDirty: Long,     // Dalvik 共享脏内存 (MB)
    val nativePrivateDirty: Long,    // Native 私有脏内存 (MB)
    val otherPrivateDirty: Long      // 其他私有脏内存 (MB)
) {
    /**
     * 获取 Java 堆内存使用率（百分比）
     */
    fun getHeapUsagePercent(): Float {
        return if (javaHeapMax > 0) {
            (javaHeapUsed.toFloat() / javaHeapMax.toFloat()) * 100
        } else 0f
    }

    /**
     * 获取系统内存使用率（百分比）
     */
    fun getSystemMemoryUsagePercent(): Float {
        return if (systemTotalMemory > 0) {
            ((systemTotalMemory - systemAvailableMemory).toFloat() / systemTotalMemory.toFloat()) * 100
        } else 0f
    }

    /**
     * 格式化为详细字符串
     */
    fun toDetailedString(): String {
        return buildString {
            appendLine("Memory Snapshot")
            appendLine("=" .repeat(50))
            appendLine("Time: ${timestamp}")
            appendLine()
            appendLine("Java Heap:")
            appendLine("  Used: ${javaHeapUsed}MB / ${javaHeapMax}MB (${String.format("%.1f", getHeapUsagePercent())}%)")
            appendLine("  Total: ${javaHeapTotal}MB")
            appendLine("  Free: ${javaHeapFree}MB")
            appendLine()
            appendLine("PSS Memory: ${pssMemory / 1024}MB")
            appendLine()
            appendLine("System Memory:")
            appendLine("  Available: ${systemAvailableMemory}MB / ${systemTotalMemory}MB")
            appendLine("  Usage: ${String.format("%.1f", getSystemMemoryUsagePercent())}%")
            appendLine()
            appendLine("Native Memory:")
            appendLine("  Heap Size: ${nativeHeapSize}MB")
            appendLine("  Allocated: ${nativeHeapAlloc}MB")
        }
    }
}

// 辅助数据类
data class SystemMemoryInfo(val available: Long, val total: Long)
data class HeapMemoryInfo(
    val dalvikPrivateDirty: Long,
    val dalvikSharedDirty: Long,
    val nativePrivateDirty: Long,
    val otherPrivateDirty: Long
)
data class NativeMemoryInfo(val heapSize: Long, val allocSpace: Long)
data class ProcessMemoryStatus(val vmRSS: Long, val vmSize: Long, val threads: Int)
