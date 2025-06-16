package arch.cayenne.lib.base.utils.ext

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

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
                    val intercepted = onIntercept()
                    if (intercepted) return
                    // 放行自己，并触发系统默认行为
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        )
    }

}