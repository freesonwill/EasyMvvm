package com.xcjh.app.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {

    private var animator: ValueAnimator? = null
    private var textWidth = 0f
    private var animatorValue = 1f // 初始值为 1，即文本完全隐藏

    init {
        setSingleLine(true)
        setHorizontallyScrolling(true)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        textWidth = paint.measureText(text.toString())
        startAnimation()
    }

    override fun onDraw(canvas: Canvas ) {
        canvas?.let {
            val x = width * animatorValue - textWidth // 从右侧开始显示
            it.save()
            it.translate(x, 0f)
            super.onDraw(it)
            it.restore()
        }
    }

    private fun startAnimation() {
        animator?.cancel()
        val durationMillis = calculateDurationBasedOnTextLength() // 根据文本长度计算动画持续时间
        animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = durationMillis
            interpolator = AccelerateDecelerateInterpolator()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                animatorValue = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun calculateDurationBasedOnTextLength(): Long {
        // 根据文本长度计算动画持续时间，这只是一个示例，你可以根据需要调整逻辑
        val baseDuration = 5000L // 基础持续时间，例如5秒
        val textLength = text.length // 文本长度
        val minDuration = 1000L // 最小持续时间，例如1秒
        val maxDuration = 10000L // 最大持续时间，例如10秒

        // 使用一个简单的线性关系来调整持续时间
        // 更复杂的逻辑可以基于文本的实际宽度和视图的宽度来计算
        val adjustedDuration = Math.max(minDuration, Math.min(maxDuration, baseDuration * (1 - textLength / (maxOf(1, textLength) * 2))))
        return adjustedDuration
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }
}