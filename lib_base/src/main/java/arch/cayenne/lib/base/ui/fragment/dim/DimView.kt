package arch.cayenne.lib.base.ui.fragment.dim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DimView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG) // 加入抗鋸齒
    private val holePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private var holeRectF: RectF? = null
    private var cornerRadius = 0f

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        dimPaint.color = Color.BLACK
        holePaint.xfermode = porterDuffXfermode
    }

    fun setRect(centerX: Int, centerY: Int, width: Int, height: Int, radius: Float) {
        val left = centerX - width / 2f
        val top = centerY - height / 2f
        val right = centerX + width / 2f
        val bottom = centerY + height / 2f

        this.holeRectF = RectF(left, top, right, bottom)
        this.cornerRadius = radius
        invalidate()
    }

    fun clearRect() {
        this.holeRectF = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)
        holeRectF?.let {
            canvas.drawRoundRect(it, cornerRadius, cornerRadius, holePaint)
        }
    }
}