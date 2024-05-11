package com.xcjh.app.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.CPUReq

class PerformanceMonitor(private val context: Context) {

    companion object {
        private const val TAG = "PerformanceMonitor"
        private const val INTERVAL = 1000 // 每秒更新一次
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false

    private val monitorRunnable = object : Runnable {
        override fun run() {
            if (isMonitoring) {
                val cpuUsage = getCpuUsage()
                val memoryUsage = getMemoryUsage()

                // 将 CPU 使用率转换为百分比
                val cpuUsagePercent = "${"%.1f".format(cpuUsage * 100)}%"
                // 将内存使用量转换为 MB
                val memoryUsageMB = ProcessCpuInfo().convertToMB(memoryUsage)

                // 在这里可以将 CPU 和内存的使用情况输出到日志或更新 UI
                Log.d(TAG, "CPU Usage: $cpuUsagePercent")
                val formattedNumber = String.format("%.2f", memoryUsageMB)


                Log.d(TAG, "Memory Usage: $formattedNumber MB")
                var cpu= CPUReq()
                cpu.cpu=cpuUsagePercent
                cpu.memory=formattedNumber


                appViewModel.cpuEvent.postValue(cpu)
                handler.postDelayed(this, INTERVAL.toLong())
            }
        }
    }

    fun startMonitoring() {
        isMonitoring = true
        handler.post(monitorRunnable)
    }

    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(monitorRunnable)
    }

    private fun getCpuUsage(): Double {
        val processCpuInfo = ProcessCpuInfo()
        val totalCpuUsage = processCpuInfo.getTotalCpuUsage()
        val numCores = Runtime.getRuntime().availableProcessors()
        return totalCpuUsage / numCores.toDouble() // 平均每个核心的 CPU 使用情况
    }

    private fun getMemoryUsage(): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem - memoryInfo.availMem // 总内存减去可用内存
    }

    private class ProcessCpuInfo {
        private var lastCpuTime: Long = 0
        private var lastUpTime: Long = System.nanoTime() // 初始化为当前时间的纳秒表示

        public fun getTotalCpuUsage(): Double {
            val totalTime = Debug.threadCpuTimeNanos()
            val upTime = System.nanoTime()
            val cpuUsage: Double

            if (lastUpTime > 0) {
                val elapsedTime = upTime - lastUpTime // 计算时间间隔，单位为纳秒
                val cpuElapsedTime = totalTime - lastCpuTime // 计算 CPU 时间间隔
                cpuUsage = if (elapsedTime > 0) {
                    // 将 CPU 使用时间间隔转换为秒
                    val cpuElapsedTimeSeconds = cpuElapsedTime / 1e9
                    // 计算平均 CPU 使用率百分比，并确保结果在 0 到 100 之间
                    val averageCpuUsagePercent = (cpuElapsedTimeSeconds * 100.0 / elapsedTime).coerceIn(0.0, 100.0)
                    averageCpuUsagePercent
                } else {
                    0.0 // 如果时间间隔为零或负值，则 CPU 使用率为零
                }
            } else {
                cpuUsage = 0.0 // 如果是首次检查，CPU 使用率为零
            }

            // 更新 lastCpuTime 和 lastUpTime
            lastCpuTime = totalTime
            lastUpTime = upTime

            return cpuUsage
        }

        // 将内存值转换为兆字节（MB）
        fun convertToMB(bytes: Long): Double {
            return bytes / (1024.0 * 1024.0)
        }
    }
}