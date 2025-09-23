package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

/**
 * @date: 2025/7/30 15:49
 * @description: 换肤业务实现类
 */
class SkinnableBizBackgroundImpl(private val view:View): ISkinnableBiz {
    lateinit var backgroundHelper: SkinnableBackGroundHelper private set
    lateinit var flowHelper:SkinnableViewFlowHelper private set

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        flowHelper = SkinnableViewFlowHelper()
        backgroundHelper = SkinnableBackGroundHelper(view)
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
    }
}