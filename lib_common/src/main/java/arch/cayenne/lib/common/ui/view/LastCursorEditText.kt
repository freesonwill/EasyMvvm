package arch.cayenne.lib.common.ui.view

/**
 * @date: 2025/8/29 11:12
 * @description:
 */
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class LastCursorEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: android.graphics.Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused && text != null) {
            setSelection(text!!.length)
        }
    }
}
