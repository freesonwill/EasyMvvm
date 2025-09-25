package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.helper.SkinnableLanguageFlowHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import java.util.Locale

/**
 * @author: wenxi
 * @date: 5/9/25 17:23
 * @description:
 */
class SkinnableBizTextImpl(private val view: TextView) : SkinnableBizBackgroundImpl(view),ISkinnableTextBiz {
    private lateinit var mTextHelper: SkinnableTextHelper
    private lateinit var languageHelper:SkinnableLanguageFlowHelper

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super.initView(context, attrs, defStyleAttr)
        mTextHelper = SkinnableTextHelper(view)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)
        languageHelper = SkinnableLanguageFlowHelper()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        languageHelper.startLanguageFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            updateLanguage(it)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        languageHelper.destroyFlow()
    }

    override fun setTextAppearance(resId: Int) {
        mTextHelper.onSetTextAppearance(view.context, resId)
    }

    override fun setTextAppearance(context: Context, resId: Int) {
        mTextHelper.onSetTextAppearance(context, resId)
    }

    override fun setTextRes(stringRes: Int, vararg formatArgs: Any) {
        mTextHelper.updateText(stringRes, *formatArgs)
    }

    override fun setHintRes(stringRes: Int, vararg formatArgs: Any) {
        mTextHelper.updateHint(stringRes, *formatArgs)
    }

    override fun setTextColorRes(color: Int) {
        mTextHelper.setTextColor(color)
    }

    override fun updateLanguage(locale: Locale) {
        mTextHelper.updateLanguage(locale)
    }

    override fun setFontWeight(weight: Int) {
        mTextHelper.setFontWeight(weight)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        start: Int,
        top: Int,
        end: Int,
        bottom: Int
    ) {
        mTextHelper.onSetCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        mTextHelper.onSetCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
    }

    override fun updateSkin(msgType: SkinMsgType) {
        super.updateSkin(msgType)
        mTextHelper.updateSkin(msgType)
    }
}