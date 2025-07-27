package arch.cayenne.lib.perf

import android.content.Context
import android.os.Looper
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.perf.monitor.FrameDropLogger
import arch.cayenne.lib.perf.monitor.FPSMonitor
import arch.cayenne.lib.perf.monitor.MemoryMonitor

/**
 * @date: 2025/7/27 13:42
 * @description:
 */
class PerfModuleInitializer : DefaultInitializer<String> {
    private val TAG = this::class.java.simpleName

    override fun create(context: Context): String {
        "$TAG init....".logd(TAG)
        enablePerfMonitor()
        detectFrameDrop()
        return TAG
    }

    /**
     * 启动性能监控
     */
    private fun enablePerfMonitor() {
        if (BuildConfig.PERF_FPS_MONITOR) {
            val fpsMonitor = FPSMonitor()
            fpsMonitor.start()
        }
        if (BuildConfig.PERF_MEMORY_MONITOR) {
            val memoryMonitor = MemoryMonitor(1000)
            memoryMonitor.start()
        }
    }


    private fun detectFrameDrop(){
        if(BuildConfig.PERF_FRAME_DROP_MONITOR) {
            val frameDropLogger = FrameDropLogger()
            frameDropLogger.start()
            Looper.getMainLooper().setMessageLogging(FrameDropLogger.LooperMonitor())
        }
    }
}