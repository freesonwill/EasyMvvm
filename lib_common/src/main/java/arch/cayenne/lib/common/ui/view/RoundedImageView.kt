package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import arch.cayenne.lib.common.R
class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var cornerRadius = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0)
        cornerRadius = a.getDimension(R.styleable.RoundedImageView_roundedCornerRadius, 0f)
        a.recycle()

        // 关键：开启 outline 裁剪
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
        clipToOutline = true
    }

    // 支持动态修改圆角（可选）
    fun setCornerRadius(radiusDp: Float) {
        cornerRadius = radiusDp.dpToPx(context)
        invalidateOutline()
    }

    private fun Float.dpToPx(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }
}