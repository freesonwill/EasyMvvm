package com.walisport.module.live.ui.widget

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import com.walisport.module.live.utils.EmojiUtils


class EmojiTextView :
    AppCompatTextView {
    private val textHelper: SkinnableTextHelper = SkinnableTextHelper(this)
    private val backGroundHelper: SkinnableBackGroundHelper = SkinnableBackGroundHelper(this)
    private val flowHelper = SkinnableViewFlowHelper()

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
      flowHelper.startSkinFlow {
          backGroundHelper.updateSkin()
          textHelper.updateSkin()
      }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backGroundHelper.loadFromAttributes(attrs, defStyleAttr)
        textHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        var builder = SpannableString(text)
        if (!text.isNullOrEmpty()) {
            EmojiUtils.replaceEmoji(context, builder, )
        }
        super.setText(builder, type)
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }
}