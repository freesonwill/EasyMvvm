package arch.cayenne.lib.base.utils.ext

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logw
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author: zhangsan
 * @date: 2025/5/28 10:13
 * @description:
 */
object FragmentExt {
    private val TAG = "FragmentExt"

    /**
     * Fragment是否根节点Fragment（直接附加到Activity）
     *
     */
    val Fragment.isRootFragment get() = parentFragment?.parentFragment == null


    /**
     * 处理回退事件
     *
     * @param onIntercept 拦截回退事件的逻辑，返回true表示拦截，false表示放行
     */
    fun Fragment.handleBackPressed(onIntercept: () -> Boolean) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isBackPressedDebounced()){
                        "handleOnBackPressed $this".logd(TAG)
                        return
                    }
                    val intercepted = onIntercept()
                    if (intercepted) return
                    isEnabled = false  // 放行自己，并触发系统默认行为
                    requireActivity().onBackPressedDispatcher.onBackPressed() //触发系统默认的回退逻辑
                    isEnabled = true // 这里需要重新启用回调，以便下次可以再次拦截
                }
            }
        )
    }

    /**
     * 执行防抖的导航操作
     */
    private var lastNavigateTime = 0L
    fun isNavigationDebounced(reason: String): Boolean {
        val isDebounced =  isBackPressedDebounced()
        if(!isDebounced) {
            lastNavigateTime = System.currentTimeMillis()
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            "navigation blocked by debounce,lastNavigateTime:${sdf.format(lastNavigateTime)},reason:$reason".logw(TAG)
        }
        return isDebounced
    }

    /**
     * 導航操作防返回抖動
     */
    private fun isBackPressedDebounced(): Boolean {
        return System.currentTimeMillis() - lastNavigateTime < 500L
    }

}