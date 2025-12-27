package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor

/**
 * 升级VIP所需投注额进度控件
 */

class VIPProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mHeight = 0
    private var mWidth = 0
    private val radius = 2f.dp2px
    private var mProgressValue = 0f
    private var startColor = R.color.shader_start_copper.getColor()

    private val bgPaint: Paint = Paint()
    private val mPaint: Paint = Paint()
    private var gradient: LinearGradient? = null

    init {
        bgPaint.color = R.color.shader_background.getColor()
        bgPaint.isAntiAlias = true
        mPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawProgress(canvas)
    }

    fun setProgressColor(color: Int) {
        startColor = color
    }

    fun setProgress(value: Float) {
        if (value > 100) {
            mProgressValue = 1f
        }
        this.mProgressValue = value / 100f
        invalidate()
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), radius, radius, bgPaint)
    }

    private fun drawProgress(canvas: Canvas) {
        if (mProgressValue > 0) {
            if (gradient == null) {
                val x1 = mProgressValue * mWidth
                val end = R.color.shader_start.getColor()
                gradient = LinearGradient(
                    0f, 0f, x1, 0f,
                    intArrayOf(startColor, end),
                    floatArrayOf(0.7f, 1.0f),
                    Shader.TileMode.CLAMP
                )
                mPaint.setShader(gradient)
            }
            canvas.drawRoundRect(
                0f,
                0f,
                mProgressValue * mWidth,
                mHeight.toFloat(),
                radius,
                radius,
                mPaint
            )
        }
    }
}