package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableTextHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import java.util.Locale

/**
 * @author: wenxi
 * @date: 5/9/25 17:23
 * @description:
 */
class SkinnableBizTextImpl(private val view: TextView):ISkinnableTextBiz {

    lateinit var backgroundHelper: SkinnableBackGroundHelper private set
    lateinit var flowHelper: SkinnableViewFlowHelper private set
    lateinit var mTextHelper: SkinnableTextHelper private set

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        backgroundHelper = SkinnableBackGroundHelper(view)
        mTextHelper = SkinnableTextHelper(view)
        flowHelper = SkinnableViewFlowHelper()
        backgroundHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        flowHelper.startSkinFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundHelper.updateSkin()
        }
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
    }

    override fun setTextAppearance(resId: Int) {
    }

    override fun setTextAppearance(context: Context, resId: Int) {
    }

    override fun setTextRes(stringRes: Int, vararg formatArgs: Any) {
    }

    override fun setHintRes(stringRes: Int, vararg formatArgs: Any) {
    }

    override fun setTextColorRes(color: Int) {
    }

    override fun updateLanguage(locale: Locale) {
    }

    override fun setFontWeight(weight: Int) {
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        start: Int,
        top: Int,
        end: Int,
        bottom: Int
    ) {
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
    }



    override fun updateBackground(resId: Int) {

    }

    override fun updateBackgroundTintId(resId: Int) {
    }

    override fun updateForegroundId(resId: Int) {
    }

}