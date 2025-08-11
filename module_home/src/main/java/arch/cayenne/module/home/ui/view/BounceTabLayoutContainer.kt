package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.core.view.isEmpty
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

class BounceTabLayoutContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var lastX = 0f                                                          // 上一次觸控的 X 座標
    private var initialTouchX = 0f                                                  // DOWN 當下的初始 X 座標
    private var activePointerId = MotionEvent.INVALID_POINTER_ID                    // 當前有效的 pointer id，用於多點觸控追蹤
    private var isUserTouching = false                                              // 是否正在手指觸控中
    private var isOverScrolling = false                                             // 是否處於 overScroll 狀態中
    private var startIntercept = false                                              // 是否決定攔截此事件序列
    private var hasDraggedEnough = false                                            // 是否拖動距離已超過 touchSlop 門檻
    private var totalDraggedX = 0f                                                  // 累積拖曳的距離，用來計算回彈強度
    private var velocityX = 0f                                                      // 拖曳速度（以像素/毫秒為單位）
    private var lastMoveTime = 0L                                                   // 上一次移動的時間戳，用來計算速度
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop          // 系統判定滑動動作的最小拖動距離
    private val maxOverScroll by lazy { 150 * resources.displayMetrics.density }    // 最大允許 overScroll 的距離（以 dp 表示）
    private val overScrollThreshold = 5f                                            // 觸發 overScroll 的最小移動距離
    private val tabLayout: TabLayout?
        get() = getChildAt(0) as? TabLayout
    private val canScrollLeft: Boolean
        get() = tabLayout?.canScrollHorizontally(-1) ?: false
    private val canScrollRight: Boolean
        get() {
            val layout = tabLayout ?: return false
            val slidingStrip = layout.getChildAt(0) as? ViewGroup ?: return false
            if (slidingStrip.isEmpty()) return false

            val lastTab = slidingStrip.getChildAt(slidingStrip.childCount - 1) ?: return false

            // 取得 slidingStrip 的 translationX（因為可能被 scroll 移動）
            val stripTranslationX = slidingStrip.translationX

            // 最後一個 tab 的右邊界
            val lastTabRight = lastTab.right

            // 可視範圍的右邊界（扣掉 paddingRight）
            val visibleRight = layout.width - layout.paddingRight + layout.scrollX

            return lastTabRight > visibleRight + 1 // 加 1 是容錯
        }

    init {
        // 初始化時設置邊緣滑動停止檢查器
        doOnLayout {
            setupEdgeBounceOnScrollStop()
        }
    }

    /**
     * 嘗試攔截觸控事件，根據是否在邊界並且有明確滑動意圖來決定
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val layout = tabLayout ?: return super.onInterceptTouchEvent(ev)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                layout.animate().cancel()
                resetTouchState()
                isUserTouching = true
                hasDraggedEnough = false
                lastX = ev.x
                initialTouchX = ev.x
                activePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val index = ev.findPointerIndex(activePointerId)
                if (index == -1) return false

                val x = ev.getX(index)
                val dx = x - lastX
                val totalDx = abs(x - initialTouchX)

                if (totalDx > touchSlop) hasDraggedEnough = true

                updateVelocity(dx)

                if (isUserTouching && hasDraggedEnough && shouldOverScroll(dx)) {
                    startIntercept = true
                    lastX = x
                    return true
                }

                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> resetTouchState()
        }

        return false
    }

    /**
     * 實際處理滑動行為與彈性動畫邏輯
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val layout = tabLayout ?: return super.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                layout.animate().cancel()
                resetTouchState()
                lastX = event.x
                initialTouchX = event.x
                activePointerId = event.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(activePointerId)
                if (index == -1) return false

                val x = event.getX(index)
                val dx = x - lastX

                if (startIntercept || shouldOverScroll(dx)) {
                    isOverScrolling = true
                    applyDampingTranslation(layout, dx)
                }

                totalDraggedX += abs(dx)
                updateVelocity(dx)
                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isOverScrolling && totalDraggedX > 10f) {
                    animateBounceBack()
                } else {
                    resetTouchState()
                }
            }
        }

        return isOverScrolling || startIntercept
    }

    /**
     * 根據位移 dx 與時間差，更新 velocityX（速度）
     */
    private fun updateVelocity(dx: Float) {
        val now = System.currentTimeMillis()
        val dt = now - lastMoveTime
        if (dt > 0) velocityX = dx / dt
        lastMoveTime = now
    }

    /**
     * 對 layout 套用 damping（阻尼）效果
     */
    private fun applyDampingTranslation(layout: View, dx: Float) {
        val percentPulled = (layout.translationX / maxOverScroll).coerceIn(-1f, 1f)
        val resistance = 1f - abs(percentPulled)
        val damping = 0.15f + 0.35f * resistance * resistance * resistance
        val newTranslation = layout.translationX + dx * damping
        layout.translationX = newTranslation.coerceIn(-maxOverScroll, maxOverScroll)
    }

    /**
     * 根據「拖曳距離」與「滑動速度」來動態計算出彈回動畫的初始位移（bounce offset），
     * 讓回彈效果根據用戶的操作力道呈現不同的強度。
     */
    private fun computeBounceOffset(): Float {
        val dragFactor = (totalDraggedX / maxOverScroll).coerceIn(0f, 1.5f)
        val velocityFactor = (abs(velocityX) * 1000).coerceIn(0f, 100f)

        val weightedPower = 0.3f * dragFactor + 0.7f * (velocityFactor / 100f)
        return (20f + weightedPower * 80f).coerceIn(50f, 300f)
    }

    /**
     * 執行彈性回彈動畫，並根據拖曳強度調整初始位移
     */
    private fun animateBounceBack() {
        tabLayout?.let { layout ->
            val offset = computeBounceOffset()
            val duration = (200 + (offset / 100f) * 200).toLong().coerceIn(200, 400)

            if (layout.translationX == 0f && !isOverScrolling) {
                val directionOffset = when {
                    !canScrollLeft -> offset
                    !canScrollRight -> -offset
                    else -> 0f
                }
                layout.translationX = directionOffset
            }

            layout.animate()
                .translationX(0f)
                .setDuration(duration)
                .setInterpolator(OvershootInterpolator(2f))
                .withEndAction { resetTouchState() }
                .start()
        }
    }

    /**
     * 判斷是否應該觸發 overScroll 邏輯
     */
    private fun shouldOverScroll(dx: Float): Boolean {
        val canScrollLeft = canScrollLeft
        val canScrollRight = canScrollRight
        val isAtLeft = !canScrollLeft && dx > overScrollThreshold
        val isAtRight = !canScrollRight && dx < -overScrollThreshold
        val isNearLeft = !canScrollLeft && dx > 0
        val isNearRight = !canScrollRight && dx < 0

        return isOverScrolling || isAtLeft || isAtRight || isNearLeft || isNearRight
    }

    /**
     * 重置整個觸控與狀態變數
     */
    private fun resetTouchState() {
        isOverScrolling = false
        startIntercept = false
        isUserTouching = false
        hasDraggedEnough = false
        activePointerId = MotionEvent.INVALID_POINTER_ID
        totalDraggedX = 0f
    }

    /**
     * 監控 TabLayout 停止滑動後，若處於邊緣時觸發補償動畫
     */
    private fun setupEdgeBounceOnScrollStop() {
        val layout = tabLayout ?: return
        var lastScrollX = layout.scrollX
        var scrollIdleRunnable: Runnable? = null

        layout.viewTreeObserver.addOnScrollChangedListener {
            val currentScrollX = layout.scrollX
            if (currentScrollX != lastScrollX) {
                lastScrollX = currentScrollX
                scrollIdleRunnable?.let { layout.removeCallbacks(it) }

                scrollIdleRunnable = Runnable {
                    if (!canScrollLeft || !canScrollRight) onScrollStoppedIfAtEdge()
                }

                layout.doOnLayout {
                    layout.postDelayed(scrollIdleRunnable, 30L)
                }
            }
        }
    }

    /**
     * 滾動結束且在邊緣時，檢查是否需要觸發補償動畫
     */
    private fun onScrollStoppedIfAtEdge() {
        tabLayout?.let { layout ->
            if (layout.translationX != 0f && !isOverScrolling) {
                animateBounceBack()
                return
            }

            val isAtEdge = !canScrollLeft || !canScrollRight
            if (isUserTouching && hasDraggedEnough && isAtEdge && layout.translationX == 0f) {
                animateBounceBack()
            }

            if (!isUserTouching && isAtEdge && layout.translationX == 0f) {
                totalDraggedX = (abs(velocityX) * 1000 * 30).coerceIn(20f, 300f)
                animateBounceBack()
            }
        }
    }
}