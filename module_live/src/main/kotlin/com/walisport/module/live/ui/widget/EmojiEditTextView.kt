package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import com.walisport.module.live.utils.EmojiUtils

class EmojiEditTextView : AppCompatEditText {
   private val textHelper: SkinnableTextHelper = SkinnableTextHelper(this)
   private val flowHelper:SkinnableViewFlowHelper = SkinnableViewFlowHelper()

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
           textHelper.updateSkin()
       }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        textHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (!getText().isNullOrEmpty()) {
            EmojiUtils.replaceEmoji(context, getText()!!)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        flowHelper.destroyFlow()
    }
}