package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

class BounceTabLayoutContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var lastX = 0f                                                      // 最後一次觸控點的 X 座標
    private var isOverScrolling = false                                         // 是否處於過度滑動狀態（回彈中）
    private var activePointerId = MotionEvent.INVALID_POINTER_ID                // 當前有效的觸控指標 ID
    private var startIntercept = false                                          // 是否開始攔截觸控事件（進入回彈模式）
    private var initialTouchX = 0f                                              // 初始觸控點 X 座標
    private val maxOverScroll by lazy { 150 * resources.displayMetrics.density } // 最大允許的過度滑動距離（dp），提供更明顯的回彈效果
    private val overScrollThreshold = 5f                                        // 判斷是否進入回彈狀態的滑動距離閾值（dp）

    private val tabLayout: TabLayout?
        get() = getChildAt(0) as? TabLayout

    init {
        doOnLayout {
            setupEdgeBounceOnScrollStop()
        }
    }

    // 攔截觸控事件的邏輯，用來決定是否進入回彈模式
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        tabLayout?.let {
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // 初始化觸控狀態
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

                    // 若符合回彈條件則攔截事件
                    if (shouldOverScroll(it, dx)) {
                        startIntercept = true
                        lastX = x
                        return true
                    }

                    lastX = x
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 結束時重置狀態
                    resetState()
                }
            }
            return false
        } ?: return super.onInterceptTouchEvent(ev)
    }

    // 真正處理滑動與回彈的邏輯
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        tabLayout?.let {
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

                    // 若已進入或應該進入回彈狀態，則模擬阻尼感
                    if (startIntercept || shouldOverScroll(it, dx)) {
                        isOverScrolling = true
                        val percentPulled = (it.translationX / maxOverScroll).coerceIn(-1f, 1f)
                        val resistance = 1f - abs(percentPulled)
                        val cubicDamping = resistance * resistance * resistance // 越接近邊界越小
                        val dampingFactor = 0.15f + 0.35f * cubicDamping        // 最大只有 0.5，最小 0.15
                        val newTranslation = it.translationX + dx * dampingFactor
                        it.translationX = newTranslation.coerceIn(-maxOverScroll, maxOverScroll)
                    }

                    lastX = x
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isOverScrolling) {
                        animateBounceBack()
                    } else {
                        resetState()
                    }
                }
            }
            return isOverScrolling || startIntercept
        } ?: return super.onTouchEvent(event)
    }

    // 設置滑動監聽，當滑動到邊界時觸發觸底回彈效果
    private fun setupEdgeBounceOnScrollStop() {
        tabLayout?.let { layout ->
            var lastScrollX = layout.scrollX
            var scrollIdleRunnable: Runnable? = null

            layout.viewTreeObserver.addOnScrollChangedListener {
                val currentScrollX = layout.scrollX
                if (currentScrollX != lastScrollX) {
                    lastScrollX = currentScrollX

                    scrollIdleRunnable?.let { layout.removeCallbacks(it) }
                    scrollIdleRunnable = Runnable {
                        val canScrollLeft = layout.canScrollHorizontally(-1)
                        val canScrollRight = layout.canScrollHorizontally(1)

                        if ((!canScrollLeft || !canScrollRight)) {
                            onScrollStoppedIfAtEdge()
                        }
                    }
                    layout.doOnLayout {
                        layout.postDelayed(scrollIdleRunnable, 30L)
                    }
                }
            }
        } ?: return
    }

    private fun triggerFlingBounce() {
        tabLayout?.let { layout ->
            if (layout.translationX == 0f && !isOverScrolling) {
                val directionOffset = when {
                    !layout.canScrollHorizontally(-1) -> 12f    // 在左邊邊界，往右彈
                    !layout.canScrollHorizontally(1) -> -12f    // 在右邊邊界，往左彈
                    else -> 0f
                }
                layout.translationX = directionOffset
            }

            animateBounceBack()
        } ?: return
    }

    private fun onScrollStoppedIfAtEdge() {
        tabLayout?.let { layout ->
            if (layout.translationX != 0f && !isOverScrolling) {
                // 已經偏移，觸發彈回
                triggerFlingBounce()
                return
            }

            // 判斷是否在邊界但還沒偏移（剛好滑到底）
            val canScrollLeft = layout.canScrollHorizontally(-1)
            val canScrollRight = layout.canScrollHorizontally(1)
            val isAtEdge = !canScrollLeft || !canScrollRight

            if (isAtEdge && layout.translationX == 0f) {
                triggerFlingBounce()
            }
        } ?: return
    }

    // 回彈動畫，將 TabLayout 移回原位
    private fun animateBounceBack() {
        tabLayout?.let { layout ->
            // 根據方向調整幅度
            val distance = 50f
            val bounceDistance =
                if (layout.translationX < 0) -1 * distance else distance

            layout.animate()
                .translationXBy(bounceDistance)
                .setDuration(100)
                .withEndAction {
                    layout.animate()
                        .translationX(0f)
                        .setDuration(280)
                        .setInterpolator { input ->
                            val factor = 1f - input
                            (1f - factor * factor * factor) * (1f - 0.2f * factor)
                        }
                        .withEndAction {
                            resetState()
                        }
                        .start()
                }
                .start()
        } ?: return
    }

    // 重置內部狀態
    private fun resetState() {
        isOverScrolling = false
        startIntercept = false
        activePointerId = MotionEvent.INVALID_POINTER_ID
        // 確保 UI 回到初始位置
        tabLayout?.translationX = 0f
    }

    // 判斷是否應該進入過度滑動（回彈）狀態
    private fun shouldOverScroll(tabLayout: View, dx: Float): Boolean {
        val canScrollLeft = tabLayout.canScrollHorizontally(-1)
        val canScrollRight = tabLayout.canScrollHorizontally(1)

        // 僅在不能再向左／右滑動的邊界情況下，並且滑動方向正確才進入回彈
        val isAtLeftEdge = !canScrollLeft && dx > overScrollThreshold
        val isAtRightEdge = !canScrollRight && dx < -overScrollThreshold

        if (isOverScrolling) return true

        // 更寬鬆的條件，用於提高回彈的靈敏度
        val isNearLeftEdge = !canScrollLeft && dx > 0
        val isNearRightEdge = !canScrollRight && dx < 0

        return isAtLeftEdge || isAtRightEdge || isNearLeftEdge || isNearRightEdge
    }
} 