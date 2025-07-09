package arch.cayenne.module.home.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class BounceTabLayoutContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var lastX = 0f
    private var isOverScrolling = false
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var startIntercept = false
    private var initialTouchX = 0f
    private val maxOverScroll by lazy { 60 * resources.displayMetrics.density } // 增加最大拉動距離到 80dp
    private val overScrollThreshold = 5f // 降低觸發回彈的閾值到 5dp，提高響應性

    /**
     * 是否啟用回彈效果，預設 true。外部可動態設置。
     */
    var enableBounce: Boolean = true

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!enableBounce) return false
        val tabLayout = getChildAt(0) ?: return super.onInterceptTouchEvent(ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                resetState()
                lastX = ev.x
                initialTouchX = ev.x
                activePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex == -1) return false
                val x = ev.getX(pointerIndex)
                val dx = x - lastX
                val totalDx = x - initialTouchX

                // 檢查是否應該攔截觸摸事件
                if (shouldOverScroll(tabLayout, dx, totalDx)) {
                    startIntercept = true
                    lastX = x
                    return true
                }
                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 重置狀態
                resetState()
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!enableBounce) return super.onTouchEvent(event)
        val tabLayout = getChildAt(0) ?: return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                resetState()
                lastX = event.x
                initialTouchX = event.x
                activePointerId = event.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex == -1) return false
                val x = event.getX(pointerIndex)
                val dx = x - lastX
                val totalDx = x - initialTouchX

                if (startIntercept || shouldOverScroll(tabLayout, dx, totalDx)) {
                    isOverScrolling = true
                    // 使用更平滑的阻尼效果
                    val dampingFactor = 0.7f // 增加阻尼因子，讓回彈更明顯
                    val newTranslation = tabLayout.translationX + dx * dampingFactor
                    tabLayout.translationX = newTranslation.coerceIn(-maxOverScroll, maxOverScroll)
                }
                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isOverScrolling) {
                    // 使用更自然的回彈動畫
                    tabLayout.animate()
                        .translationX(0f)
                        .setDuration(200) // 稍微增加動畫時間
                        .setInterpolator { input ->
                            // 使用彈性插值器
                            val factor = 1f - input
                            (1f - factor * factor * factor) * (1f - 0.2f * factor)
                        }
                        .withEndAction {
                            // 動畫結束後重置狀態
                            resetState()
                        }
                        .start()
                } else {
                    // 如果沒有回彈，也要重置狀態
                    resetState()
                }
            }
        }
        return isOverScrolling || startIntercept
    }

    private fun resetState() {
        isOverScrolling = false
        startIntercept = false
        activePointerId = MotionEvent.INVALID_POINTER_ID
        // 確保 translationX 被重置
        getChildAt(0)?.translationX = 0f
    }

    private fun shouldOverScroll(tabLayout: View, dx: Float, totalDx: Float): Boolean {
        val canScrollLeft = tabLayout.canScrollHorizontally(-1)
        val canScrollRight = tabLayout.canScrollHorizontally(1)

        // 檢查是否在邊界且滑動距離超過閾值
        val isAtLeftEdge = !canScrollLeft && dx > overScrollThreshold
        val isAtRightEdge = !canScrollRight && dx < -overScrollThreshold

        // 如果已經開始回彈，繼續處理
        if (isOverScrolling) return true

        // 更寬鬆的邊界檢測，提高響應性
        val isNearLeftEdge = !canScrollLeft && dx > 0
        val isNearRightEdge = !canScrollRight && dx < 0

        return isAtLeftEdge || isAtRightEdge || isNearLeftEdge || isNearRightEdge
    }
} 