package arch.cayenne.module.account.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.Paint
import android.graphics.Color
import kotlin.math.min


class SystemAvatarCircleImg @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val clipPath = Path()
    private val drawableRect = RectF()

    private var rotationAngle: Float = 0f

    private val borderPaint = Paint().apply {
        color = Color.WHITE // White border
        style = Paint.Style.STROKE
        strokeWidth = 2 * resources.displayMetrics.density // 2dp
        isAntiAlias = true
    }

    init {
        scaleType = ScaleType.CENTER_CROP   // 必须是 CENTER_CROP
    }

    fun getRotationAngle(): Float {
        return rotationAngle
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 强制使用较小的边作为直径 → 保证不会拉伸变形
        val size = minOf(w, h).toFloat()
        val left = (w - size) / 2f
        val top = (h - size) / 2f

        drawableRect.set(left, top, left + size, top + size)

        clipPath.reset()
        clipPath.addCircle(
            drawableRect.centerX(),
            drawableRect.centerY(),
            size / 2f,
            Path.Direction.CW
        )
    }

    fun setRotationAngle(angle: Float) {
        rotationAngle = angle
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(rotationAngle, width / 2f, height / 2f)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)

        // Draw white border
        val radius = min(width, height) / 2f - borderPaint.strokeWidth / 2
        canvas.drawCircle(width / 2f, height / 2f, radius, borderPaint)
        canvas.restore()
    }

    fun getRotatedBitmap(): Bitmap? {
        if (width == 0 || height == 0) return null
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val outputCanvas = Canvas(outputBitmap)
        // 让 ImageView 自己渲染内容，保证和 onDraw 完全一致
        draw(outputCanvas)
        return outputBitmap
    }
}