package arch.cayenne.module.home.ui.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import kotlin.math.abs
import arch.cayenne.lib.common.R as CommonR

/**
 * 聯賽字母索引，支持跟手滑動動畫
 *
 * 功能：
 * - 顯示字母索引列表
 * - 支持觸摸選擇字母
 * - 跟手滑動動畫
 * - 選中字母最大位移，相鄰字母遞減位移
 * - 優化的動畫性能，避免抖動
 *
 * 動畫效果：
 * - 使用圓公式計算進度，實現柔順的跟手滑動
 * - 前後各四個字母會觸發動態向左位移（總共9個字母）
 * - 手勢上下移動時字母會柔順地向左位移滑出
 * - 手勢結束時字母會逐漸向右回到原位
 * - 離開9個字母範圍後柔順回到原始位置
 *
 * 優化特點：
 * - 完成 40% 以上才開始放大，提供更好的層次區別度
 * - 根據距離調整縮放和位移，增強視覺層次感
 * - 手勢點下時也有柔順的左移滑出效果
 * - 完全避免動畫衝突
 * - 柔順的跟手響應
 * - 自然的動畫過渡
 * - 即時同步觸發，消除閃動延遲
 * - 動態即時同步跟手柔順動畫
 */
@Keep
class TournamentIndexView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val letters = mutableListOf<Char>()
    private val viewMap = mutableMapOf<Char, View>()
    private val viewAnimators = mutableMapOf<Char, ValueAnimator>()

    // 添加位移状态追踪
    private val currentTranslations = mutableMapOf<Char, Float>()
    private val currentScales = mutableMapOf<Char, Float>()

    private var lastTouchedIndex = -1
    private var selectedLetter: Char? = null
    private var isUserTouching: Boolean = false
    private var isAnimating: Boolean = false
    private var lastAnimationTime: Long = 0 // 添加動畫節流
    private var lastTouchTime: Long = 0 // 添加觸摸時間追蹤

    // 触摸速度追踪和动画预测
    private var touchVelocity: Float = 0f
    private var lastTouchY: Float = 0f
    private var predictedNextIndex: Int = -1
    private var isHighSpeedScrolling: Boolean = false

    // 優化：添加動畫狀態追蹤，確保即時同步
    private var lastTouchProgress: Float = 0f
    private var isAnimationDirty: Boolean = false
    private var pendingAnimationUpdates = mutableMapOf<Char, Triple<Float, Float, Float>>()

    var onLetterTouch: ((Char) -> Unit)? = null
    var onTouchStart: (() -> Unit)? = null

    companion object {
        private const val MAX_SCALE = 1.6f // 最大縮放比例
        private const val ANIMATION_DURATION = 350L // 優化：更快的過渡動畫，提升響應速度
        private const val FAST_ANIMATION_DURATION = 200L // 優化：更快的跟手動畫，立即響應
        private const val ITEM_WIDTH_DP = 24
        private const val ITEM_HEIGHT_DP = 18
        private const val TEXT_SIZE_SP = 11f
        private const val TOUCH_PREDICTION_THRESHOLD = 30L // 優化：進一步減少觸摸預測閾值，提升響應速度

        // 跟手滑動動畫參數
        private const val MAX_ACTIVE_DISTANCE_MULTIPLIER = 4f // 最大觸發範圍倍數
        private const val SELECTED_ALPHA_MIN = 0.1f // 接近選中時的透明度
        private const val SELECTED_SCALE_MAX = 1.6f // 選中時最大放大倍率

        // 優化：動畫同步參數
        private const val ANIMATION_SYNC_THRESHOLD = 0.01f // 動畫同步閾值
    }

    // 跟手側移動畫啟用狀態
    private var isDragAnimationActive = false

    init {
        orientation = VERTICAL
        gravity = Gravity.END
        isClickable = true
        isFocusable = true
    }

    /**
     * 設置字母列表
     */
    fun setLetters(newLetters: List<Char>) {
        if (letters == newLetters) return
        
        letters.clear()
        letters.addAll(newLetters)
        buildViews()
    }

    /**
     * 設置選中的字母
     */
    fun setSelectedLetter(letter: Char?) {
        // 避免重複設置相同的字母
        if (selectedLetter == letter) return

        selectedLetter = letter
        updateSelection()

        // 優化：在觸摸狀態下立即應用動畫效果
        // 這樣可以實現手勢點下時立即發動放大和透明度效果
        if (isUserTouching) {
            // 立即應用跟手側移效果
            applyDragEffect(letter)
        }
    }

    /**
     * 獲取當前選中的字母
     */
    fun getSelectedLetter(): Char? = selectedLetter

    /**
     * 構建視圖
     */
    private fun buildViews() {
        removeAllViews()
        viewMap.clear()
        clearAnimations()

        // 重置位移和缩放状态
        currentTranslations.clear()
        currentScales.clear()

        letters.forEachIndexed { index, letter ->
            val view = createLetterView(letter, index)
            viewMap[letter] = view
            // 初始化位移和缩放状态
            currentTranslations[letter] = 0f
            currentScales[letter] = 1f
            addView(view)
        }
        updateSelection()
    }

    /**
     * 創建字母視圖
     */
    private fun createLetterView(letter: Char, index: Int): View {
        return if (letter == '*') {
            ImageView(context).apply {
                layoutParams = LayoutParams(ITEM_WIDTH_DP.dp2px, ITEM_HEIGHT_DP.dp2px).apply {
                    gravity = Gravity.END
                    // 為第一個字母添加上方邊距，避免放大時被切到
                    topMargin = ((MAX_SCALE - 1f) * ITEM_HEIGHT_DP.dp2px / 2f).toInt()
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        } else {
            TextView(context).apply {
                layoutParams = LayoutParams(ITEM_WIDTH_DP.dp2px, ITEM_HEIGHT_DP.dp2px).apply {
                    gravity = Gravity.END
                    // 為最後一個字母添加下方邊距，避免放大時被切到
                    if (index == letters.size - 1) {
                        bottomMargin = ((MAX_SCALE - 1f) * ITEM_HEIGHT_DP.dp2px / 2).toInt()
                    }
                }
                text = letter.toString()
                textSize = TEXT_SIZE_SP
                gravity = Gravity.CENTER
            }
        }
    }

    /**
     * 更新選中狀態
     */
    private fun updateSelection() {
        letters.forEach { letter ->
            val view = viewMap[letter] ?: return@forEach
            val isSelected = letter == selectedLetter

            when (view) {
                is ImageView -> {
                    val resourceId = if (isSelected) {
                        R.drawable.ic_hot_league_index
                    } else {
                        R.drawable.ic_hot_league_index_unselect
                    }
                    view.setImageResource(resourceId)
                }

                is TextView -> {
                    val colorResId = if (isSelected) {
                        CommonR.color.color_00E0E5
                    } else {
                        CommonR.color.color_00E0E54D
                    }
                    view.setTextColor(SkinnableResourceManager.getColor(context, colorResId))
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || letters.isEmpty()) return false

        // 擴大觸控區域寬度，確保高速滑動時不會滑出範圍
        val touchWidth = 40.dp2px
        val touchStartX = width - touchWidth
        val isInTouchArea = event.x >= touchStartX

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInTouchArea) {
                    isUserTouching = true
                    lastTouchTime = System.currentTimeMillis()
                    lastTouchY = event.y
                    touchVelocity = 0f
                    predictedNextIndex = -1
                    isHighSpeedScrolling = false
                    lastTouchProgress = 0f
                    isAnimationDirty = false
                    pendingAnimationUpdates.clear()
                    
                    onTouchStart?.invoke()
                    val index = calculateTouchIndex(event.y)
                    val letter = letters.getOrNull(index) ?: return false

                    lastTouchedIndex = index
                    onLetterTouch?.invoke(letter)
                    setSelectedLetter(letter)

                    // 實現手勢點下時立即發動放大和透明度效果
                    // 前後四個字母立即開始動態變化
                    applyDragEffect(letter)
                    return true
                }
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (isInTouchArea) {
                    val currentTime = System.currentTimeMillis()
                    val timeDelta = currentTime - lastTouchTime

                    if (timeDelta > 0) {
                        val yDelta = event.y - lastTouchY
                        touchVelocity = yDelta / timeDelta
                        isHighSpeedScrolling =
                            kotlin.math.abs(touchVelocity) > 0.6f // 降低高速滾動閾值
                    }

                    lastTouchY = event.y
                    lastTouchTime = currentTime

                    val index = calculateTouchIndex(event.y)
                    val letter = letters.getOrNull(index) ?: return false

                    if (index != lastTouchedIndex) {
                        lastTouchedIndex = index
                        onLetterTouch?.invoke(letter)

                        if (isHighSpeedScrolling && timeDelta < TOUCH_PREDICTION_THRESHOLD) {
                            predictedNextIndex = predictNextIndex(index, touchVelocity)
                        }
                    }

                    // 更新選中的字母
                    setSelectedLetter(letter)

                    // 前後四個字母在手勢移動過程中持續有動態變化
                    applyDragEffect(letter)
                    return true
                } else {
                    if (lastTouchedIndex != -1) {
                        postDelayed({
                            if (!isUserTouching) {
                                clearTouchState()
                            }
                        }, 300)
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 離開觸摸狀態，但保留選中的字母
                isUserTouching = false
                lastTouchedIndex = -1

                // 直接使用回位效果：字母逐漸回到原位
                resetDragAnimationsToOriginal()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 觸摸索引計算 - 跟隨手勢滑動的 y 軸位置
     * 根據手勢的 y 軸位置來選中最接近的字母
     * 確保在邊界情況下能正確選中最末端字母
     */
    private fun calculateTouchIndex(y: Float): Int {
        if (letters.isEmpty()) return 0

        // 獲取第一個和最後一個字母的 View 位置，用於邊界判斷
        val firstView = viewMap[letters.firstOrNull()] ?: return 0
        val lastView = viewMap[letters.lastOrNull()] ?: return 0

        val firstViewTop = firstView.top.toFloat()
        val lastViewBottom = lastView.bottom.toFloat()

        // 如果手勢位置在列表上方，選中第一個字母
        if (y <= firstViewTop) {
            return 0
        }

        // 如果手勢位置在列表下方，選中最後一個字母
        if (y >= lastViewBottom) {
            return letters.size - 1
        }

        // 使用實際的 View 位置來計算最接近的字母
        var closestIndex = 0
        var minDistance = Float.MAX_VALUE

        for (i in letters.indices) {
            val letter = letters[i]
            val view = viewMap[letter] ?: continue

            // 獲取 View 在父容器中的實際位置
            val viewTop = view.top.toFloat()
            val viewBottom = view.bottom.toFloat()
            val viewCenter = (viewTop + viewBottom) / 2f

            val distance = kotlin.math.abs(y - viewCenter)
            if (distance < minDistance) {
                minDistance = distance
                closestIndex = i
            }
        }
        return closestIndex
    }

    /**
     * 預測下一个可能的位置
     */
    private fun predictNextIndex(currentIndex: Int, velocity: Float): Int {
        if (letters.isEmpty()) return currentIndex

        val direction = if (velocity > 0) 1 else -1
        val nextIndex = currentIndex + direction

        return when {
            nextIndex < 0 -> 0
            nextIndex >= letters.size -> letters.size - 1
            else -> nextIndex
        }
    }

    /**
     * 計算動畫參數 - 避免重複計算
     */
    private fun calculateAnimationParams(): Pair<Float, Float> {
        val labelHeight = ITEM_HEIGHT_DP.dp2px.toFloat()
        val labelGap = 2.dp2px.toFloat() // 字母間距
        val maxDistance = (labelHeight + labelGap) * MAX_ACTIVE_DISTANCE_MULTIPLIER

        // 選中時向左移動的最大距離
        // 但是我們需要限制位移距離，避免字母完全消失
        val maxShiftX = -60.dp2px.toFloat() // 最大向左位移 100dp
        val shiftX = kotlin.math.max(-maxDistance, maxShiftX)

        return Pair(maxDistance, shiftX)
    }

    /**
     * 圓公式計算進度 - 透過圓公式計算所有字母向左位移完成度
     * 達到手勢選中字母 touch down 時 9 個向左位移的字母
     */
    private fun calculateProgress(distance: Float, maxDistance: Float): Float {
        return if (maxDistance > 0) {
            val normalizedDistance = distance / maxDistance
            val clampedDistance = normalizedDistance.clamped(0f..1f)

            // 使用 easeOutQuart 變體來計算進度，讓跟手效果更自然
            // 在接近選中位置時進度變化更敏感，提供更自然的透明度過渡
            val progress =
                1f - clampedDistance * clampedDistance * clampedDistance * clampedDistance

            // 確保進度在有效範圍內，並提供更平滑的過渡
            progress.clamped(0f..1f)
        } else {
            0f
        }
    }

    /**
     * 動畫進度應用
     * 統一處理位移、縮放和透明度的應用，確保動畫效果的一致性
     */
    private fun applyAnimationProgress(
        view: View,
        letter: Char,
        progress: Float,
        shiftX: Float,
        scaleFactor: Float,
        alpha: Float
    ) {
        // progress <= 0 時重置為原始狀態
        if (progress <= 0f) {
            view.translationX = 0f
            view.scaleX = 1f
            view.scaleY = 1f
            view.alpha = 1f
            currentTranslations[letter] = 0f
            currentScales[letter] = 1f
            return
        }

        // progress >= 1 時應用完整效果
        if (progress >= 1f) {
            view.translationX = shiftX
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
            view.alpha = 1f
            currentTranslations[letter] = shiftX
            currentScales[letter] = scaleFactor
            return
        }

        // 0 < progress < 1 時的漸進效果
        val clampedProgress = progress.clamped(0f..1f)

        // 完成 40% 以上要開始放大
        val scaleThreshold = 0.4f
        val scaleProgress =
            kotlin.math.max((clampedProgress - scaleThreshold) / (1f - scaleThreshold), 0f)
        val scaleT = lerp(1f, scaleFactor, scaleProgress)

        // 應用變換：位移 * progress，縮放使用計算出的 scaleT
        view.translationX = shiftX * clampedProgress
        view.scaleX = scaleT
        view.scaleY = scaleT

        // alpha 計算方式
        view.alpha = alpha + (1f - clampedProgress) * (1f - alpha)

        // 更新記錄的狀態
        currentTranslations[letter] = shiftX * clampedProgress
        currentScales[letter] = scaleT
    }

    /**
     * 重置字母到原始位置
     */
    private fun resetToOriginalPosition(view: View, letter: Char) {
        val currentScale = currentScales[letter] ?: 1f
        val currentTranslationX = currentTranslations[letter] ?: 0f
        val currentAlpha = view.alpha

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ANIMATION_DURATION
            interpolator = FastOutSlowInInterpolator()

            addUpdateListener { animation ->
                val progress = animation.animatedFraction

                // 回到原始狀態
                val newScale = lerp(currentScale, 1f, progress)
                val newTranslationX = lerp(currentTranslationX, 0f, progress)
                val newAlpha = lerp(currentAlpha, 1f, progress)

                view.scaleX = newScale
                view.scaleY = newScale
                view.translationX = newTranslationX
                view.alpha = newAlpha

                // 更新記錄的狀態
                currentScales[letter] = newScale
                currentTranslations[letter] = newTranslationX
            }
        }

        animator.start()
    }

    /**
     * 根據距離調整縮放因子
     */
    private fun getAdjustedScaleFactor(distance: Int): Float {
        return when (distance) {
            0 -> SELECTED_SCALE_MAX // 選中字母最大縮放
            1 -> SELECTED_SCALE_MAX * 0.9f // 相鄰字母適度縮放
            2 -> SELECTED_SCALE_MAX * 0.8f // 再相鄰字母輕微縮放
            3 -> SELECTED_SCALE_MAX * 0.65f // 更遠字母更輕微縮放
            4 -> SELECTED_SCALE_MAX * 0.45f // 最遠字母最輕微縮放
            else -> 1f // 無縮放
        }
    }

    /**
     * 根據距離調整位移距離
     */
    private fun getAdjustedShiftX(baseShiftX: Float, distance: Int): Float {
        return when (distance) {
            0 -> baseShiftX // 選中字母最大位移
            1 -> baseShiftX * 0.98f // 相鄰字母適度位移，與選中字母有明顯區別
            2 -> baseShiftX * 0.92f // 再相鄰字母輕微位移，保持視覺層次
            3 -> baseShiftX * 0.82f // 更遠字母更輕微位移
            4 -> baseShiftX * 0.5f // 最遠字母最輕微位移
            else -> 0f // 無位移y
        }
    }

    /**
     * 重置所有動畫到原始位置
     */
    private fun resetDragAnimationsToOriginal() {
        isDragAnimationActive = false

        letters.forEach { letter ->
            val view = viewMap[letter] ?: return@forEach
            resetToOriginalPosition(view, letter)
        }
    }

    /**
     * 以 maxDistance 為主要選中字母，前後各四個字母會觸發跟手滑動時動態向左位移的柔順效果
     * 跟隨手勢：根據手勢滑動的 y 軸位置來選中字母
     */
    private fun applyDragEffect(selectedLetter: Char?) {
        if (selectedLetter == null) return

        val selectedIndex = letters.indexOf(selectedLetter)
        if (selectedIndex == -1) return

        isDragAnimationActive = true

        val (maxDistance, baseShiftX) = calculateAnimationParams()

        for (i in letters.indices) {
            val letter = letters[i]
            val view = viewMap[letter] ?: continue
            val distance = abs(i - selectedIndex)

            // 前後四個字母都會觸發動畫效果
            if (distance <= 4) {
                if (isUserTouching) {
                    val touchY = lastTouchY
                    val viewCenterY = view.top + view.height / 2f
                    val distanceFromTouch = kotlin.math.abs(touchY - viewCenterY) - view.height / 2f

                    // 計算手勢移動的進度，讓前後字母持續有動態變化
                    val gestureProgress = calculateGestureProgress(touchY, viewCenterY, maxDistance)

                    val adjustedShiftX = getAdjustedShiftX(baseShiftX, distance)

                    val progress = calculateProgress(distanceFromTouch, maxDistance)

                    // 結合手勢進度，讓前後字母持續有動態變化
                    val finalProgress = progress * gestureProgress

                    if (finalProgress <= 0f) {
                        // 超出觸發範圍：使用動畫進度應用，progress = 0 重置為原始狀態
                        applyAnimationProgress(view, letter, 0f, 0f, 1f, 1f)
                        continue
                    }

                    if (finalProgress >= 1f) {
                        val targetScale = getAdjustedScaleFactor(distance)
                        val targetAlpha = 1f // 選中時透明度為1

                        // 使用動畫進度應用，確保選中字母達到最大效果
                        applyAnimationProgress(
                            view,
                            letter,
                            1.0f,
                            adjustedShiftX,
                            targetScale,
                            targetAlpha
                        )
                        continue
                    }

                    // 根據速度調整動畫效果
                    val velocityAdjustedProgress = adjustProgressByVelocity(finalProgress)

                    // 使用動畫進度應用，確保動畫效果的一致性
                    val targetScale = getAdjustedScaleFactor(distance)
                    // 透明度邏輯：選中字母前後兩個字母最淡化，離原始位置越近的字母越靠近原本初始顏色
                    val targetAlpha = when {
                        distance <= 2 -> {
                            // 選中字母及其前後兩個字母：根據進度計算透明度
                            // 進度越大，透明度越接近 1（選中狀態）
                            SELECTED_ALPHA_MIN + (1f - SELECTED_ALPHA_MIN) * velocityAdjustedProgress
                        }
                        else -> {
                            // 更遠的字母：保持較高的透明度，接近原始狀態
                            0.9f + 0.1f * velocityAdjustedProgress
                        }
                    }

                    // 使用動畫進度應用，統一處理位移、縮放和透明度
                    applyAnimationProgress(
                        view,
                        letter,
                        velocityAdjustedProgress,
                        adjustedShiftX,
                        targetScale,
                        targetAlpha
                    )
                }
            } else {
                // 不在9個字母：使用動畫進度應用，progress = 0 重置為原始狀態
                if (isUserTouching) {
                    // 使用動畫進度應用，progress = 0 會重置為原始狀態
                    applyAnimationProgress(view, letter, 0f, 0f, 1f, 1f)
                }
            }
        }
    }

    /**
     * 新增：計算手勢移動的進度
     * 讓前後四個字母在手勢移動過程中持續有動態變化
     */
    private fun calculateGestureProgress(
        touchY: Float,
        viewCenterY: Float,
        maxDistance: Float
    ): Float {
        val distanceFromTouch = kotlin.math.abs(touchY - viewCenterY)

        // 如果手勢在字母中心附近，進度為1
        if (distanceFromTouch <= 10.dp2px) {
            return 1f
        }

        // 計算手勢進度，使用更平滑的曲線
        val normalizedDistance = distanceFromTouch / maxDistance
        val clampedDistance = normalizedDistance.clamped(0f..1f)

        // 使用 easeOutQuart 變體來計算進度，讓跟手效果更自然
        val progress = 1f - clampedDistance * clampedDistance * clampedDistance * clampedDistance

        return progress.clamped(0f..1f)
    }


    /**
     * 當用戶快速滑動時，提供更流暢的動畫預測
     */
    private fun adjustProgressByVelocity(progress: Float): Float {
        if (!isHighSpeedScrolling) return progress

        // 根據速度調整進度，讓動畫更流暢
        val velocityFactor = kotlin.math.abs(touchVelocity).coerceIn(0f, 2f) / 2f
        val adjustedProgress = progress + (1f - progress) * velocityFactor * 0.3f

        return adjustedProgress.clamped(0f..1f)
    }


    /**
     * 清除所有動畫
     */
    private fun clearAnimations() {
        viewAnimators.values.forEach { it.cancel() }
        viewAnimators.clear()
        isAnimating = false

        letters.forEach { letter ->
            val view = viewMap[letter] ?: return@forEach
            val scale = currentScales[letter] ?: 1f
            val translationX = currentTranslations[letter] ?: 0f

            view.scaleX = scale
            view.scaleY = scale
            view.translationX = translationX
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimations()
        clearTouchState()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            clearTouchState()
        }
    }

    /**
     * 確保狀態重置的一致性，避免殘留的動畫狀態
     */
    private fun clearTouchState() {
        lastTouchedIndex = -1
        isUserTouching = false
        lastTouchProgress = 0f
        isAnimationDirty = false
        pendingAnimationUpdates.clear()

        // 取消所有正在進行的動畫，避免衝突
        viewAnimators.values.forEach { it.cancel() }
        viewAnimators.clear()

        // 直接使用效果：重置所有動畫
        resetDragAnimationsToOriginal()

        // 重置所有位移和缩放状态到初始值
        letters.forEach { letter ->
            currentTranslations[letter] = 0f
            currentScales[letter] = 1f
        }

        touchVelocity = 0f
        lastTouchY = 0f
        predictedNextIndex = -1
        isHighSpeedScrolling = false
        lastAnimationTime = 0L
    }


    /**
     * 進度限制 - 限制上下邊界，超出邊界就會使用邊界的值
     */
    private fun Float.clamped(range: ClosedRange<Float>): Float {
        return when {
            this < range.start -> range.start
            this > range.endInclusive -> range.endInclusive
            else -> this
        }
    }

    /**
     * 線性插值 - 在兩個值之間進行線性插值
     */
    private fun lerp(start: Float, end: Float, t: Float): Float {
        return start + (end - start) * t
    }
}


