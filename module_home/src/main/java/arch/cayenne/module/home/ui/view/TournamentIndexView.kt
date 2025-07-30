package arch.cayenne.module.home.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Keep
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import kotlin.math.abs

/**
 * 聯賽字母索引，支持放大鏡效果
 *
 * 功能：
 * - 顯示字母索引列表
 * - 支持觸摸選擇字母
 * - 提供放大鏡視覺效果
 * - 選中字母最大位移，相鄰字母遞減位移
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
    
    private var lastTouchedIndex = -1
    private var selectedLetter: Char? = null
    private var isUserTouching: Boolean = false

    var onLetterTouch: ((Char) -> Unit)? = null
    var onTouchStart: (() -> Unit)? = null

    companion object {
        private const val MAX_SCALE = 1.8f // 最大縮放比例
        private const val ANIMATION_DURATION = 150L // 動畫時長
        private const val ITEM_WIDTH_DP = 24
        private const val ITEM_HEIGHT_DP = 18
        private const val TEXT_SIZE_SP = 11f
        private const val MAX_TRANSLATION_X_DP = 80 // 選中字母的最大位移
    }

    private val maxTranslationX = MAX_TRANSLATION_X_DP.dp2px

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
        selectedLetter = letter
        updateSelection()
    }

    /**
     * 構建視圖
     */
    private fun buildViews() {
        removeAllViews()
        viewMap.clear()
        clearAnimations()

        letters.forEachIndexed { index, letter ->
            val view = createLetterView(letter, index)
            viewMap[letter] = view
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
                        arch.cayenne.lib.common.R.color.brand_color
                    } else {
                        R.color.brand_color_index_unselect
                    }
                    view.setTextColor(SkinnableResourceManager.getColor(context, colorResId))
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || letters.isEmpty()) return false

        // 限制觸控區域寬度
        val touchWidth = 36.dp2px
        val touchStartX = width - touchWidth
        val isInTouchArea = event.x >= touchStartX

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInTouchArea) {
                    isUserTouching = true
                    onTouchStart?.invoke()
                    val index = calculateTouchIndex(event.y)
                    val letter = letters.getOrNull(index) ?: return false

                    lastTouchedIndex = index
                    onLetterTouch?.invoke(letter)
                    setSelectedLetter(letter)
                    applyMagnifierEffect(letter)
                    return true
                }
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (isInTouchArea) {
                    val index = calculateTouchIndex(event.y)
                    val letter = letters.getOrNull(index) ?: return false

                    if (index != lastTouchedIndex) {
                        lastTouchedIndex = index
                        onLetterTouch?.invoke(letter)
                        setSelectedLetter(letter)
                        applyMagnifierEffect(letter)
                    }
                    return true
                } else {
                    // 滑出觸控區域，重置效果
                    if (lastTouchedIndex != -1) {
                        clearTouchState()
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 無論是否在觸控區域內，都要重置效果
                clearTouchState()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 觸摸索引計算 - 考慮實際 View 位置
     */
    private fun calculateTouchIndex(y: Float): Int {
        if (letters.isEmpty()) return 0

        // 使用實際的 View 位置來計算
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
     * 應用放大鏡效果
     */
    private fun applyMagnifierEffect(selectedLetter: Char) {
        val selectedIndex = letters.indexOf(selectedLetter)
        if (selectedIndex == -1) return

        letters.forEachIndexed { index, letter ->
            val view = viewMap[letter] ?: return@forEachIndexed

            // 取消之前的動畫
            cancelAnimation(letter)

            // 創建新的動畫
            createMagnifierAnimation(view, index, selectedIndex, letter)
        }
    }

    /**
     * 創建放大鏡動畫
     */
    private fun createMagnifierAnimation(view: View, index: Int, selectedIndex: Int, letter: Char) {
        val distance = abs(index - selectedIndex)

        // 計算圓弧縮放 - 與位移保持一致
        val scale = when (distance) {
            0 -> MAX_SCALE // 選中字母最大縮放
            1 -> 1.4f // 相鄰字母適度縮放
            2 -> 1.1f // 再相鄰字母輕微縮放
            else -> 1f // 更遠字母無縮放
        }

        // 計算圓弧位移
        val translationX = calculateTranslation(index, selectedIndex)

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                view.scaleX = 1f + (scale - 1f) * progress
                view.scaleY = 1f + (scale - 1f) * progress
                view.translationX = translationX * progress
            }
        }

        viewAnimators[letter] = animator
        animator.start()
    }

    /**
     * 計算圓弧位移 - 所有字母都向左位移
     */
    private fun calculateTranslation(index: Int, selectedIndex: Int): Float {
        val distance = abs(index - selectedIndex)

        return when {
            index == selectedIndex -> {
                // 選中的字母：向左最大位移
                -maxTranslationX.toFloat()
            }

            distance == 1 -> {
                // 相鄰字母：向左適度位移
                -maxTranslationX.toFloat() * 0.8f
            }

            distance == 2 -> {
                // 再相鄰字母：向左輕微位移
                -maxTranslationX.toFloat() * 0.4f
            }

            else -> {
                // 更遠的字母：無位移
                0f
            }
        }
    }

    /**
     * 重置放大鏡效果
     */
    private fun resetMagnifierEffect() {
        letters.forEach { letter ->
            val view = viewMap[letter] ?: return@forEach
            cancelAnimation(letter)

            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = ANIMATION_DURATION
                interpolator = DecelerateInterpolator()
                addUpdateListener { animation ->
                    val progress = animation.animatedValue as Float
                    view.scaleX = 1f + (1f - view.scaleX) * progress
                    view.scaleY = 1f + (1f - view.scaleY) * progress
                    view.translationX *= (1f - progress)
                }
            }

            viewAnimators[letter] = animator
            animator.start()
        }
    }

    /**
     * 取消動畫
     */
    private fun cancelAnimation(letter: Char) {
        viewAnimators[letter]?.cancel()
    }

    /**
     * 清除所有動畫
     */
    private fun clearAnimations() {
        viewAnimators.values.forEach { it.cancel() }
        viewAnimators.clear()
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
     * 清理觸摸狀態
     */
    private fun clearTouchState() {
        lastTouchedIndex = -1
        selectedLetter = null
        resetMagnifierEffect()
        updateSelection()
    }
}

