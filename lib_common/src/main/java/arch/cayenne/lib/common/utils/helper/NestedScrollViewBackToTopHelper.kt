package arch.cayenne.lib.common.utils.helper




import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.NestedScrollView
import androidx.core.view.isVisible
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

/**
 * 回到頂部按鈕輔助類（適用於 NestedScrollView）
 */
class NestedScrollViewBackToTopHelper(
    val nestedScrollView: NestedScrollView,
    val button: AppCompatImageView,
    val onBackToTop: (() -> Unit)? = null
) {
    private var totalDy = 0
    private val alphaRunnable = Runnable {
        button.alpha = 1f
    }
    init {
            nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
                val height = v.height
                totalDy = scrollY
                if (totalDy > height) {
                    if (!button.isVisible) {
                        button.visibility = View.VISIBLE
                    }
                    // 滚动中透明度
                    button.alpha = 0.3f
                    // 停止滚动后恢复透明度
                    v.removeCallbacks(alphaRunnable)
                    v.postDelayed(alphaRunnable, 150)
                } else {
                    if (button.isVisible) {
                        button.visibility = View.GONE
                    }
                }
            }

        button.clickNoRepeat {
            nestedScrollView.smoothScrollTo(0, 0)
            onBackToTop?.invoke()
            button.visibility = View.GONE
        }
        }

    fun reset() {
        totalDy = 0
    }
}
