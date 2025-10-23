package arch.cayenne.lib.common.ui.view

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

    companion object {
        private const val MIN_PULL_DISTANCE = 0.005f
        private const val BOUNCE_DURATION = 400L // 增加動畫時長，讓彈動速度再慢一點點
        private const val DAMPING_FACTOR = 0.5f // 降低阻尼因子，讓拉動更敏感
        private const val MAX_TRANSLATION_RATIO = 0.25f // 增加最大拉動距離比例
        private const val CLICK_THRESHOLD = 8f // 點擊判定閾值，比 touchSlop 小
        private const val OVERSHOOT_TENSION = 1.6f // 降低 OvershootInterpolator 的張力參數，讓回彈更柔和
        private const val PULL_SENSITIVITY = 1.0f // 調整拉動敏感度為 1.0f，確保拖動距離的一致性
    }

    // 可配置的回彈參數
    var bounceDistanceRatio: Float = MAX_TRANSLATION_RATIO
        set(value) {
            field = value.coerceIn(0.1f, 0.5f) // 限制在合理範圍內
            updateMaxTranslation()
        }

    var bounceDampingFactor: Float = DAMPING_FACTOR
        set(value) {
            field = value.coerceIn(0.3f, 0.8f) // 限制在合理範圍內
        }

    var bounceDuration: Long = BOUNCE_DURATION
        set(value) {
            field = value.coerceIn(200L, 500L) // 限制在合理範圍內
        }

    var overshootTension: Float = OVERSHOOT_TENSION
        set(value) {
            field = value.coerceIn(1.0f, 3.0f) // 限制在合理範圍內
        }

    var pullSensitivity: Float = PULL_SENSITIVITY
        set(value) {
            field = value.coerceIn(0.8f, 2.0f) // 限制在合理範圍內
        }

    private var lastX = 0f
    private var initialTouchX = 0f
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isOverScrolling = false
    private var totalPullDistance = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var maxTranslation = 0f
    private var isAnimating = false
    private var isPulling = false
    private var isClickable = true // 是否為點擊事件
    private var hasMoved = false // 是否已經移動
    private var isAtLeftEdge = false
    private var isAtRightEdge = false

    init {
        doOnLayout {
            updateEdgeState()
            updateMaxTranslation()
        }
    }

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

            // 最後一個 tab 的右邊界
            val lastTabRight = lastTab.right

            // 可視範圍的右邊界（扣掉 paddingRight）
            val visibleRight = layout.width - layout.paddingRight + layout.scrollX

            return lastTabRight > visibleRight + 1 // 加 1 是容錯
        }

    /**
     * 更新邊界狀態
     */
    private fun updateEdgeState() {
        isAtLeftEdge = !canScrollLeft
        isAtRightEdge = !canScrollRight
    }

    /**
     * 更新最大拉動距離
     */
    private fun updateMaxTranslation() {
        maxTranslation = width * bounceDistanceRatio
    }

    /**
     * 嘗試攔截觸控事件，根據是否在邊界並且有明確滑動意圖來決定
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val layout = tabLayout ?: return super.onInterceptTouchEvent(ev)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                layout.animate().cancel()
                resetState()
                lastX = ev.x
                initialTouchX = ev.x
                activePointerId = ev.getPointerId(0)
                isClickable = true
                hasMoved = false
                updateEdgeState()
            }

            MotionEvent.ACTION_MOVE -> {
                val index = ev.findPointerIndex(activePointerId)
                if (index == -1) return false

                val x = ev.getX(index)
                val dx = x - lastX
                val totalDx = abs(x - initialTouchX)

                // 檢查是否移動超過點擊閾值
                if (totalDx > CLICK_THRESHOLD) {
                    hasMoved = true
                    isClickable = false
                }

                // 關鍵邏輯：在邊界時立即攔截，但避免誤攔截點擊
                if (hasMoved && shouldOverScroll(dx)) {
                    lastX = x
                    return true
                }

                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 如果是點擊事件，不攔截
                if (isClickable && !hasMoved) {
                    return false
                }
                resetState()
            }
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
                resetState()
                lastX = event.x
                initialTouchX = event.x
                activePointerId = event.getPointerId(0)
                isClickable = true
                hasMoved = false
                updateEdgeState()
            }

            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(activePointerId)
                if (index == -1) return false

                val x = event.getX(index)
                val dx = x - lastX
                val totalDx = abs(x - initialTouchX)

                // 檢查是否移動超過點擊閾值
                if (totalDx > CLICK_THRESHOLD) {
                    hasMoved = true
                    isClickable = false
                }

                // 實時更新邊界狀態
                updateEdgeState()

                // 處理 overscroll
                if (shouldOverScroll(dx)) {
                    isOverScrolling = true
                    applyPullEffect(layout, dx)
                }

                lastX = x
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isOverScrolling && totalPullDistance > MIN_PULL_DISTANCE) {
                    startBounceAnimation()
                } else {
                    resetState()
                }
            }
        }

        // 只有在處理 overscroll 時才返回 true
        return isOverScrolling
    }

    /**
     * 應用拉動效果
     */
    private fun applyPullEffect(layout: View, dx: Float) {
        if (isAnimating) return

        val sign = when {
            isAtLeftEdge && dx > 0 -> 1      // 左邊界向右拉動
            isAtRightEdge && dx < 0 -> -1    // 右邊界向左拉動
            else -> return
        }

        // 使用敏感度參數讓拉動更流暢
        val deltaDistance = abs(dx) / width * pullSensitivity
        totalPullDistance += deltaDistance

        // 使用可配置的阻尼因子
        val targetTranslation = sign * width * totalPullDistance * bounceDampingFactor
        val clampedTranslation = targetTranslation.coerceIn(-maxTranslation, maxTranslation)

        layout.translationX = clampedTranslation
        isPulling = true
    }

    /**
     * 開始回彈動畫
     */
    private fun startBounceAnimation() {
        isAnimating = true
        isPulling = false

        tabLayout?.let { layout ->
            layout.animate()
                .translationX(0f)
                .setDuration(bounceDuration)
                .setInterpolator(OvershootInterpolator(overshootTension))
                .withEndAction {
                    resetState()
                }
                .start()
        }
    }

    /**
     * 判斷是否應該觸發 overScroll - 優化邊界檢測
     */
    private fun shouldOverScroll(dx: Float): Boolean {
        // 如果已經在 overScroll 狀態，繼續允許
        if (isOverScrolling) return true

        // 精確的邊界檢測：在邊界且有拖動意圖時觸發
        return (isAtLeftEdge && dx > 0) || (isAtRightEdge && dx < 0)
    }

    /**
     * 重置所有狀態
     */
    private fun resetState() {
        isAnimating = false
        isOverScrolling = false
        isPulling = false
        totalPullDistance = 0f
        isClickable = true
        hasMoved = false
        tabLayout?.translationX = 0f
        activePointerId = MotionEvent.INVALID_POINTER_ID
    }

    /**
     * 設置是否跳過動畫
     */
    fun setSkipAnim(skip: Boolean) {
        isAnimating = skip
        if (skip) {
            tabLayout?.translationX = 0f
            resetState()
        }
        updateEdgeState()
    }

    /**
     * 設置回彈效果參數
     */
    fun setBounceConfig(
        distanceRatio: Float? = null,
        dampingFactor: Float? = null,
        duration: Long? = null,
        tension: Float? = null,
        sensitivity: Float? = null
    ) {
        distanceRatio?.let { bounceDistanceRatio = it }
        dampingFactor?.let { bounceDampingFactor = it }
        duration?.let { bounceDuration = it }
        tension?.let { overshootTension = it }
        sensitivity?.let { pullSensitivity = it }

        // 立即更新最大拉動距離
        updateMaxTranslation()
    }
}