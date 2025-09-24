package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

/**
 * @date: 2025/7/30 15:49
 * @description: 换肤业务实现类
 */
open class SkinnableBizBackgroundImpl(private val view:View): ISkinnableBiz {
    private lateinit var backgroundHelper: SkinnableBackGroundHelper private set
    protected lateinit var flowHelper:SkinnableViewFlowHelper private set

    @CallSuper
    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        flowHelper = SkinnableViewFlowHelper()
        backgroundHelper = SkinnableBackGroundHelper(view)
        backgroundHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    @CallSuper
    override fun onAttachedToWindow() {
        flowHelper.startSkinFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            updateSkin()
        }
    }

    @CallSuper
    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
    }

    @CallSuper
    final override fun setBackgroundResource(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        backgroundHelper.updateBackground(nResId)
    }

    @CallSuper
    final override fun setTintColorRes(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        backgroundHelper.updateBackgroundTintId(nResId)
    }

    @CallSuper
    final override fun setForegroundRes(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        backgroundHelper.updateForegroundId(nResId)
    }

    @CallSuper
    override fun updateSkin(msgType: SkinMsgType) {
        backgroundHelper.updateSkin(msgType)
    }

}