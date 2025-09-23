package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import java.util.Locale

/**
 * @author: wenxi
 * @date: 5/9/25 17:23
 * @description:
 */
class SkinnableBizTextImpl(private val view: TextView) : ISkinnableTextBiz {

    lateinit var backgroundHelper: SkinnableBackGroundHelper private set
    lateinit var mTextHelper: SkinnableTextHelper private set
    lateinit var flowHelper: SkinnableViewFlowHelper private set

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        flowHelper = SkinnableViewFlowHelper()
        backgroundHelper = SkinnableBackGroundHelper(view)
        mTextHelper = SkinnableTextHelper(view)
        backgroundHelper.loadFromAttributes(attrs, defStyleAttr)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)

    }

    override fun onAttachedToWindow() {
        flowHelper.startSkinFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundHelper.updateSkin()
            mTextHelper.updateSkin()
        }
        flowHelper.startLanguageFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            mTextHelper.updateLanguage(it)
        }
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
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


    override fun setBackgroundResource(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        backgroundHelper.updateBackground(nResId)
    }

    override fun setTintColorRes(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        backgroundHelper.updateBackgroundTintId(nResId)
    }

    override fun setForegroundRes(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        backgroundHelper.updateForegroundId(nResId)
    }

    override fun forceUpdateSkin() {
        backgroundHelper.updateSkin(SkinMsgType.SELF)
        mTextHelper.updateSkin(SkinMsgType.SELF)
    }

}