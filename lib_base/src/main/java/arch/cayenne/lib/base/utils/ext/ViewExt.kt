package arch.cayenne.lib.base.utils.ext

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * @date: 2025/9/30 14:29
 * @description:
 */
object ViewExt {

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
}