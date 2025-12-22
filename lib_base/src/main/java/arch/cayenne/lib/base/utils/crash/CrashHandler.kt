package arch.cayenne.lib.base.utils.crash

import android.app.Application
import android.os.Process
import arch.cayenne.lib.base.utils.LogUtils
import java.io.PrintWriter
import java.io.StringWriter

/**
 * @date: 2025/12/22 14:32
 * @description: 异常捕获处理
 */
class CrashHandler(private val app: Application) : Thread.UncaughtExceptionHandler {

    private val defaultHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            val stackTrace = StringWriter().also { sw ->
                PrintWriter(sw).use { pw ->
                    e.printStackTrace(pw)
                }
            }.toString()
            val packageName = app.packageName
            val msg = buildString {
                appendLine("FATAL EXCEPTION: ${t.name}")
                appendLine("Process: $packageName  PID: ${Process.myPid()}")
                append(stackTrace)
            }

            // \*按你们 LogUtils 的 API 调整：常见是 e(tag, msg) 或 e(tag, msg, tr)
            LogUtils.file(LogUtils.E,"AndroidRuntime", msg)
        } catch (_: Throwable) {
            // 避免二次崩溃
        } finally {
            // 交给系统默认处理（会打印 AndroidRuntime 那种格式并结束进程）
            defaultHandler?.uncaughtException(t, e)
                ?: run {
                    Process.killProcess(Process.myPid())
                    kotlin.system.exitProcess(10)
                }
        }
    }
}
