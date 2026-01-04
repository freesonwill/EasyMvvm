package arch.cayenne.module.account.view

 import android.content.Context
 import android.graphics.Canvas
 import android.graphics.Outline
 import android.graphics.Path
 import android.graphics.RectF
 import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class AvatarCircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val clipPath = Path()
    private val drawableRect = RectF()

    init {
        scaleType = ScaleType.CENTER_CROP   // 必须是 CENTER_CROP
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

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
        canvas.restore()
    }
}