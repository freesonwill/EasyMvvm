package com.walisport.module.gamedetail.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.walisport.module.gamedetail.R

/**
 * 搭配 CarouselScrollView 使用的指示器
 */
class CarouselIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint
    private var indicatorBackgroundColor: Int = Color.GRAY
    private var indicatorProgressColor: Int = Color.WHITE
    private var progressWidth: Float = -1f

    private var totalPages: Int = 1
    private var currentPage: Int = 0

    private var animator: ValueAnimator? = null
    private var animatedLeft: Float = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.CarouselIndicator) {
            indicatorBackgroundColor = getColor(R.styleable.CarouselIndicator_indicator_backgroundColor, Color.GRAY)
            indicatorProgressColor = getColor(R.styleable.CarouselIndicator_indicator_progressColor, Color.WHITE)
            progressWidth = getDimension(R.styleable.CarouselIndicator_indicator_progressWidth, -1f)
        }

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 如果寬度或高度為0，則不執行繪製
        if (viewWidth == 0f || viewHeight == 0f) {
            return
        }

        // 計算每一頁對應的寬度
        val unitWidth = viewWidth / totalPages
        // 圓角半徑為 View 高度的一半，使其兩端呈完美的半圓形
        val radius = viewHeight / 2f

        // 繪製背景長條
        paint.color = indicatorBackgroundColor
        canvas.drawRoundRect(0f, 0f, viewWidth, viewHeight, radius, radius, paint)

        // 繪製當前頁面的高亮區段
        paint.color = indicatorProgressColor
        val right = animatedLeft + (if (progressWidth == -1f) (viewWidth / totalPages) else progressWidth)
        canvas.drawRoundRect(animatedLeft, 0f, right, viewHeight, radius, radius, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    /**
     * 設置總頁數，由 CarouselScrollView 自動呼叫。
     */
    internal fun setPageCount(count: Int) {
        this.totalPages = if (count > 0) count else 1
        setCurrentPage(0, false)
    }

    /**
     * 設置當前頁面，並立即觸發重繪以顯示對應的區段。
     */
    internal fun setCurrentPage(index: Int, animated: Boolean = true) {
        // 防止 index 越界
        if (index < 0 || index >= totalPages) return
        this.currentPage = index

        animator?.cancel()

        val targetLeft = calculateTargetLeft(index)
        if (!animated) {
            // 如果不需要動畫，直接設置位置並重繪
            animatedLeft = targetLeft
            invalidate()
        } else {
            // 需要動畫，則創建並啟動 ValueAnimator
            animator = ValueAnimator.ofFloat(animatedLeft, targetLeft).apply {
                duration = 200 // 動畫時長，300毫秒
                interpolator = DecelerateInterpolator() // 使用減速插值器，效果更自然

                addUpdateListener { animation ->
                    // 在動畫的每一幀，更新 animatedLeft 的值
                    animatedLeft = animation.animatedValue as Float
                    // 觸發 onDraw 重繪
                    invalidate()
                }
                start()
            }
        }
    }


    private fun calculateTargetLeft(pageIndex: Int): Float {
        val viewWidth = width.toFloat()
        if (viewWidth == 0f || totalPages <= 1) return 0f

        return if (progressWidth == -1f) {
            val unitWidth = viewWidth / totalPages
            pageIndex * unitWidth
        } else {
            val with = viewWidth - progressWidth
            val distancePerPage = with / (totalPages - 1)
            pageIndex * distancePerPage
        }
    }
}