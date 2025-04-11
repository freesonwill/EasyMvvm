package com.walisport.module.live.widget

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.skin.SportSkinManager
import com.walisport.lib.skin.widget.helper.SportSkinBackGroundHelper
import com.walisport.lib.skin.widget.helper.SportSkinTextHelper
import com.walisport.module.live.utils.EmojiUtils
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


class EmojiTextView :
    AppCompatTextView {
    private val sportSkinManager: SportSkinManager by inject(SportSkinManager::class.java)
    private val textHelper: SportSkinTextHelper = SportSkinTextHelper(this)
    private val backGroundHelper: SportSkinBackGroundHelper = SportSkinBackGroundHelper(this)

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
                backGroundHelper.updateSkin()
                textHelper.updateSkin()
            }
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


}