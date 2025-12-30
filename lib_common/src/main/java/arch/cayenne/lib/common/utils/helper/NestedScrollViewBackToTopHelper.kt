package arch.cayenne.lib.common.utils.helper




import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import kotlin.math.abs

/**
 * 回到頂部按鈕輔助類（適用於 NestedScrollView）
 */
class NestedScrollViewBackToTopHelper(
    val nestedScrollView: NestedScrollView,
    val button: AppCompatImageView,
    val onBackToTop: (() -> Unit)? = null,
    val scrollStateListener: ((Int)-> Unit)? = null
) {
    private var totalDy = 0
    private val alphaRunnable = Runnable {
        button.alpha = 1f
    }

    private var touched = false

    private val scrollRunnable: Runnable by lazy {
        Runnable {
            if (touched) {
                // 用户正在触摸屏幕，延迟检查
                nestedScrollView.postDelayed(scrollRunnable, 150)
                return@Runnable
            }
            scrollStateListener?.invoke(RecyclerView.SCROLL_STATE_IDLE)
        }
    }

    init {
        nestedScrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                touched = false
            } else if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                touched = true
            }
            false
        }
            nestedScrollView.setOnScrollChangeListener { v, _, scrollY, _, _ ->
                val height = v.height

                if (abs(totalDy - scrollY) > TOUCH_SLOP) {
                    scrollStateListener?.invoke(RecyclerView.SCROLL_STATE_DRAGGING)
                    v.removeCallbacks(scrollRunnable)
                    v.postDelayed(scrollRunnable, 150)
                }

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

    companion object {
        private const val TOUCH_SLOP = 8f // 滑动阈值，单位像素
    }
}
