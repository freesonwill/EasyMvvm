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

/**
 * @author: wenxi
 * @date: 5/9/25 17:23
 * @description:
 */
class SkinnableBizTextImpl(private val view: TextView):ISkinnableBiz {

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

    fun updateBackground(resId: Int) {
        backgroundHelper.updateBackground(resId)
    }

    fun updateBackgroundTintId(resId: Int) {
        backgroundHelper.updateBackgroundTintId(resId)
    }

    fun updateForegroundId(resId: Int) {
        backgroundHelper.updateForegroundId(resId)
    }

}