package arch.cayenne.lib.common.utils

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Description: 线程工具类
 * author       : zhangsan
 * createTime   : 2024/6/18 15:37
 **/
object ThreadUtils {

    /**
     *  客製的CoroutineContext，用於傳遞Exception發生時的檔案名稱（TAG）
     *
     *  @property name 執行CoroutineScope的檔案名稱（TAG）
     */
    internal class CustomContext(val name: String): CoroutineContext.Element {
        companion object Key : CoroutineContext.Key<CustomContext>
        override val key: CoroutineContext.Key<*> get() = Key
    }

    /**
     *  用於捕獲執行Coroutine時發生的錯誤，配合CustomContext使用
     */
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        "Caught exception in CoroutineExceptionHandler: $throwable".loge(coroutineContext[CustomContext]?.name ?: "UNKNOWN")
        throwable.printStackTrace()
    }

    /**
     * 主线程Scope，提供给没有LifecycleScope，ViewModelScope的场景
     */
    val mainScope by lazy {
        CoroutineScope (SupervisorJob() + Dispatchers.Main + exceptionHandler)
    }

    /**
     *  監聽接口專用Scope
     */
    val appListenerScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Main + exceptionHandler)
    }

    val ioScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    }

    /**
     *  啟動使用CustomContext的Coroutine，
     *  方便傳入Tag，以便捕獲異常時辨識
     *
     *  @param tag 執行CoroutineScope的檔案名稱（TAG）
     *  @param block 要執行的程式
     *  @return 返回Job
     */
    fun CoroutineScope.launchWithCustomContext(
        tag: String, block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(CustomContext(tag)) {
            block()
        }
    }
}