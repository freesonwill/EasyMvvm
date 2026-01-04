package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.R
class ClipRoundedFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val path = Path()

    private val default :Int = 30.dp2px

    private var cornerRadius: Float = default.toFloat()

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.ClipRoundedFrameLayout)
            cornerRadius = a.getDimension(
                R.styleable.ClipRoundedFrameLayout_cornerRadius,
                default.toFloat()
            )
            a.recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updatePath()
    }

    private fun updatePath() {
        path.reset()

        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        path.addRoundRect(
            rectF,
            cornerRadius,
            cornerRadius,
            Path.Direction.CW
        )

        path.close()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 设置背景
        // canvas.drawPath(path, backgroundPaint)
    }
}