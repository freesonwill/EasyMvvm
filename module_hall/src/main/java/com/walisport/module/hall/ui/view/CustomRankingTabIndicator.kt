package com.walisport.module.hall.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.ui.view._interface.BaseCustomTabIndicator

class CustomRankingTabIndicator(context: Context, attrs: AttributeSet) : BaseCustomTabIndicator(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context,
           R.color.color_0FFFFFFF
        ) // 指示器颜色
    }

    private var indicatorWidth = 0f
    private var indicatorHeight = dpToPx(42f) // 指示器高度
    private var cornerRadius = dpToPx(30f) // 圆角半径
    private var currentPosition = 0
    private var positionOffset = 0f
    private var tabWidth = 0f
    fun getCurrentPosition(): Int {
        return currentPosition
    }
    fun setCurrentPosition(currentPosition:Int) {
         this.currentPosition = currentPosition
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
    override fun setIndicatorPosition(position: Int, offset: Float) {
        currentPosition = position
        positionOffset = offset
        invalidate()
    }

    // 设置单个 Tab 的宽度和指示器宽度
    override fun setTabWidth(width: Float,tabIndicatorWidth : Float) {
        tabWidth = width
        indicatorWidth = width * tabIndicatorWidth // 指示器宽度为 Tab 宽度的 45%
        invalidate()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}



