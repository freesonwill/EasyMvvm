package com.walisport.lib.common.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.walisport.lib.common.data.EmojiEnum
import com.walisport.lib.common.utils.ViewUtils.replaceEmoji
import com.walisport.lib.skin.SportSkinManager
import com.walisport.lib.skin.widget.helper.SportSkinBackGroundHelper
import com.walisport.lib.skin.widget.helper.SportSkinTextHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.Flow
import java.util.regex.Matcher
import java.util.regex.Pattern


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
            replaceEmoji(context, builder, textSize)
        }
        super.setText(builder, type)
    }


}