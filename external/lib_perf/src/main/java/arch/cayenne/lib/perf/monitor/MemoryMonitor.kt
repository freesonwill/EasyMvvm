package arch.cayenne.lib.perf.monitor

import android.os.Debug
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * @date: 2025/7/22 16:44
 * @description: 内存监控 （不准确，还是Android Studio Profiler）
 */
class MemoryMonitor(private val interval: Long = 1000) {
    private val TAG = this::class.java.simpleName
    private var scope:CoroutineScope? = null

    fun start() {
        scope = CoroutineScope(Dispatchers.IO)
        scope?.launch {
            while (isActive) {
                delay(interval)
                onUpdate()
                ensureActive()
            }
        }
    }

    private fun onUpdate(){
        /*
         📌 totalPss：应用占用的所有内存（包括共享库等）
         📌 nativePss：C/C++ 层使用的内存
         📌 dalvikPss：Java/Kotlin 层堆使用内存
        */
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        """
            Total Pss  = ${memoryInfo.totalPss} KB
            Dalvik Pss = ${memoryInfo.dalvikPss} KB
            Native Pss = ${memoryInfo.nativePss} KB
            Other Pss  = ${memoryInfo.otherPss} KB
        """.logd(TAG)
    }

    fun stop() {
        scope?.cancel()
    }
}