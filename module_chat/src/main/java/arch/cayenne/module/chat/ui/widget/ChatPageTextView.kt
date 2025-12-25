package arch.cayenne.module.chat.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import arch.cayenne.module.chat.R

/**
 * @author: wenxi
 * @date: 20/12/25 14:58
 * @description:
 */
class ChatPageTextView : EmojiTextView {
    var singleBack: Drawable? = null
    var multipleBack: Drawable? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        singleBack = ContextCompat.getDrawable(context, R.drawable.shape_60)
        multipleBack = ContextCompat.getDrawable(context, R.drawable.shape_12)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        updateBackground()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateBackground()
    }

    private fun updateBackground() {
        background = if (lineCount > 1) {
            multipleBack
        } else {
            singleBack
        }
    }

}