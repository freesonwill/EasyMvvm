package arch.cayenne.lib.common.utils.helper

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd


/**
 * @author: zhangsan
 * @date: 2025/5/9 12:08
 * @description: 多次操作退出App辅助类
 */
class TimesExitOnBackPressedHelper(
    private val activity: FragmentActivity,
    private val times: Int,
    private val exitThresholdMillis: Long = 2000,
    private val onExit: ((remain:Int, times:Int)->Unit)? = null
) {
    private val TAG = this.javaClass.simpleName
    private var remain: Int = times
    private var lastPressedTime = 0L

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            /*"""
                activity:${activity}
                onBackPressed:isTaskRoot:${activity.isTaskRoot}
                backStackEntryCount:${activity.supportFragmentManager.backStackEntryCount}
                exitCountIndex:$remain
            """.trimIndent().logd(TAG)*/
            if (!activity.isTaskRoot) {
                activity.finish()
                return
            }
            val now = System.currentTimeMillis()
            //超时重置
            if (now - lastPressedTime >= exitThresholdMillis) {
                remain = times
            }
            lastPressedTime = now
            remain--
            if (remain == 0) {
                ToastHelper.instance.cancelToast(activity)
                activity.finish()
                remain = times
            } else {
                onExit?.invoke(remain,times)
            }
        }
    }

    fun attach() {
        activity.onBackPressedDispatcher.addCallback(callback)
    }
}