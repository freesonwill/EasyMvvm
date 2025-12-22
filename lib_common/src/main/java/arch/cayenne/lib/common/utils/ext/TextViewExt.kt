package arch.cayenne.lib.common.utils.ext

import android.graphics.drawable.Drawable
import android.widget.TextView
import org.w3c.dom.Text

object TextViewExt {

    fun TextView.setBottomDrawable(drawable: Drawable?,drawablePadding: Int) {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(
            drawables[0], // left
            drawables[1], // top
            drawables[2], // right
            drawable      // bottom
        )
        compoundDrawablePadding = drawablePadding // 设置图片与文字的间距
    }

    fun TextView.clearBottomDrawable() {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(
            drawables[0], // left
            drawables[1], // top
            drawables[2], // right
            null          // bottom
        )
    }

    /**
     * 判断TextView是否显示了省略号
     */
    fun TextView.hasShownEllipsize():Boolean{
        val layout = layout ?: return false
        val lines = layout.lineCount
        for (i in 0 until lines) {
            if (layout.getEllipsisCount(i) > 0) {
                return true
            }
        }
        return false
    }
}