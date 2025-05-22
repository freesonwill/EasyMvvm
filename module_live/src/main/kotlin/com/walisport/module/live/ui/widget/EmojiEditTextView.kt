package com.walisport.module.live.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import com.walisport.module.live.utils.EmojiUtils
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class EmojiEditTextView : AppCompatEditText {
   private val sportSkinManager: SkinnableManager by inject(SkinnableManager::class.java)
   private val textHelper: SkinnableTextHelper = SkinnableTextHelper(this)
//   private val backGroundHelper: SkinnableBackGroundHelper = SkinnableBackGroundHelper(this)

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
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            sportSkinManager.skinFlow.collect {
//                backGroundHelper.updateSkin()
                textHelper.updateSkin()
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
//        backGroundHelper.loadFromAttributes(attrs, defStyleAttr)
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

}