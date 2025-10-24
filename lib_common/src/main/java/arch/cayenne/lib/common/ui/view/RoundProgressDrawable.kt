package arch.cayenne.lib.common.ui.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * 一個可自訂顏色、圓角的 Progress Drawable，
 * 能讓進度條兩邊都圓頭，右邊圓頭會隨進度移動。
 */
class RoundProgressDrawable(
    private val backgroundColor: Int = Color.LTGRAY,
    private val progressColor: Int = Color.MAGENTA
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progressInternal = 0f // 0f ~ 1f

    var progress: Float
        get() = progressInternal
        set(value) {
            progressInternal = value.coerceIn(0f, 1f)
//            invalidateSelf()
        }

    override fun draw(canvas: Canvas) {
        val b = bounds
        val w = b.width().toFloat()
        val h = b.height().toFloat()
        val r = h / 2f  // 圓角半徑 = 高度的一半

        // ---- 背景條（灰色） ----
        paint.color = backgroundColor
        canvas.drawRoundRect(0f, 0f, w, h, r, r, paint)

        // ---- 進度條（紅色） ----
        if (progressInternal <= 0f) return

        val progressWidth = w * progressInternal

        paint.color = progressColor

        if (progressWidth <= r) {
            // 進度很少，畫一個圓
            canvas.drawCircle(progressWidth / 2f, r, progressWidth / 2f, paint)
        } else {
            // 主體矩形 + 右邊圓頭
            val rectRight = progressWidth - r
            // 矩形部分（左圓角 + 直角右邊）
            canvas.drawRoundRect(0f, 0f, rectRight + r, h, r, r, paint)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}