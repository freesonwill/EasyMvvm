package com.walisport.module.live.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import arch.cayenne.lib.skin.widget.SkinnableView
import com.walisport.module.live.R

class CustomTabIndicator(context: Context, attrs: AttributeSet) : SkinnableView(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.tab_indicator_color) // 指示器颜色
    }

    private var indicatorWidth = 0f
    private var indicatorHeight = dpToPx(3f) // 指示器高度
    private var cornerRadius = dpToPx(2f) // 圆角半径
    private var currentPosition = 1
    private var positionOffset = 0f
    private var tabWidth = 0f
    fun getCurrentPosition(): Int {
        return currentPosition
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 计算指示器的左右边界，确保居中
        val tabCenter = (currentPosition + 0.5f) * tabWidth + positionOffset * tabWidth
        val left = tabCenter - indicatorWidth / 2
        val right = tabCenter + indicatorWidth / 2
        // 绘制圆角矩形
        canvas.drawRoundRect(
            left, height - indicatorHeight, right, height.toFloat(),
            cornerRadius, cornerRadius, paint
        )
    }

    // 设置指示器位置和偏移量
    fun setIndicatorPosition(position: Int, offset: Float) {
        currentPosition = position
        positionOffset = offset
        invalidate()
    }

    // 设置单个 Tab 的宽度和指示器宽度
    fun setTabWidth(width: Float) {
        tabWidth = width
        indicatorWidth = width * 0.6f // 指示器宽度为 Tab 宽度的 60%
        invalidate()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}



