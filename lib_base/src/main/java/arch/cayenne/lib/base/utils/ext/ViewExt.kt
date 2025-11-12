package arch.cayenne.lib.base.utils.ext

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @date: 2025/9/30 14:29
 * @description:
 */
object ViewExt {
    private const val TAG = "ViewExt"
    /**
     * 监听fitsSystemWindows变化
     */
    fun View.applyInsetsForFitsSystemWindows() {
        // 保存原始 padding
        val initialLeft = paddingLeft
        val initialTop = paddingTop
        val initialRight = paddingRight
        val initialBottom = paddingBottom

        // 定义 listener
        val insetsListener = { v: View, insets: WindowInsetsCompat ->
            if (fitsSystemWindows) {
                val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.updatePadding(
                    left = initialLeft + systemInsets.left,
                    top = initialTop + systemInsets.top,
                    right = initialRight + systemInsets.right,
                    bottom = initialBottom + systemInsets.bottom
                )
            } else {
                // fitsSystemWindows = false 时恢复初始 padding
                v.setPadding(initialLeft, initialTop, initialRight, initialBottom)
            }
            insets
        }

        // 设置 listener
        ViewCompat.setOnApplyWindowInsetsListener(this, insetsListener)

        // 请求一次 inset 应用
        requestApplyInsets()
    }


    /**
     * 更安全的post方式，防止post引起的泄漏
     * @param action
     */
    fun View.postSafely(action:Runnable){
        findViewTreeLifecycleOwner()!!.lifecycleScope.launch {
            action.run()
        }
    }

    /**
     * 更安全的post方式，防止post引起的泄漏
     * @param delayMillis
     * @param action
     */
    fun View.postDelayedSafely(delayMillis:Long,action:() -> Unit){
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            delay(delayMillis)
            action()
        } ?: let { "postDelayedSafely failed: $this has no lifecycleOwner".loge(TAG) }
    }

}