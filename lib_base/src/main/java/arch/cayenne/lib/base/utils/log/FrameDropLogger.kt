package arch.cayenne.lib.base.utils.log

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Printer
import android.view.Choreographer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge

/**
 * @date: 2025/6/30 11:02
 * @description: 掉帧检测（日志）
 */
private const val PREFIX = "[FrameDrop]:"
private const val FRAME_THRESHOLD_MS = 500L

class FrameDropLogger(private val threshold: Long = FRAME_THRESHOLD_MS) : Choreographer.FrameCallback {
    companion object {
        private const val TAG = "FrameDropLogger"
    }
    private var lastFrameTimeNanos = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val detectedRunnable = Runnable {
        val rawStack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Looper.getMainLooper().thread.stackTrace
        } else {
            Thread.currentThread().stackTrace
        }
        val filteredStack = filterStackTrace(rawStack)
        val stackTraceStr = filteredStack.joinToString("\n") { "    at $it" }
        "$PREFIX Frame dropped! stackTrace:\n$stackTraceStr".loge(TAG)
    }

    /**************** Method *****************/

    fun start() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            "$PREFIX must be started on the main thread".loge(TAG)
            return
        }
        lastFrameTimeNanos = System.nanoTime()
        Choreographer.getInstance().postFrameCallback(this)
        "$PREFIX started".logd(TAG)
    }

    fun stop() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            "$PREFIX must be started on the main thread".loge(TAG)
            return
        }
        assert(Looper.myLooper() == Looper.getMainLooper())
        lastFrameTimeNanos = System.nanoTime()
        handler.removeCallbacks(detectedRunnable)
        Choreographer.getInstance().removeFrameCallback(this)
        "$PREFIX stopped".logd(TAG)
    }

    override fun doFrame(frameTimeNanos: Long) {
        val now = System.nanoTime()
        val frameCostMs = (now - lastFrameTimeNanos) / 1_000_000L
        handler.removeCallbacks(detectedRunnable)
        if (frameCostMs > threshold) {
            handler.postDelayed(detectedRunnable, threshold)
            "$PREFIX${getSeverity(frameCostMs)} Frame dropped! Frame time = ${frameCostMs}ms".loge(TAG)
        }
        lastFrameTimeNanos = now
        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun getSeverity(frameCostMs: Long): String {
        return when {
            frameCostMs >= 700 -> "🔥严重卡顿"
            frameCostMs >= 300 -> "⚠️中度卡顿"
            frameCostMs >= 100 -> "🔹轻微卡顿"
            else -> ""
        }
    }

    private fun filterStackTrace(stackTrace: Array<StackTraceElement>): List<StackTraceElement> {
        return stackTrace.filter {
            val className = it.className
            !className.startsWith("android.") &&
                    !className.startsWith("java.") &&
                    !className.startsWith("kotlin.") &&
                    !className.contains("Choreographer") &&
                    !className.contains("FrameDropLogger") &&
                    !className.contains("Looper")
        }
    }

    /**
     * Looper检测
     */
    class LooperMonitor(private val threshold: Long = FRAME_THRESHOLD_MS) : Printer {
        companion object {
            private const val TAG = "LooperMonitor"
        }
        private var startTime = 0L

        override fun println(x: String) {
            if (x.startsWith(">>>>>")) {
                // 消息开始处理
                startTime = System.currentTimeMillis()
            } else if (x.startsWith("<<<<<")) {
                // 消息处理结束
                val cost = System.currentTimeMillis() - startTime
                if (cost > threshold) {
                    "$PREFIX Main thread blocked for $cost ms".loge(TAG)
                }
            }
        }
    }
}

