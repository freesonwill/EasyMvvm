package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

/**
 * @date: 2025/7/30 15:49
 * @description: 换肤业务实现类
 */
class SkinnableBizImpl(private val view:View): ISkinnableBiz {
    lateinit var backgroundTintHelper: SkinnableBackGroundHelper private set
    lateinit var flowHelper:SkinnableViewFlowHelper private set

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        backgroundTintHelper = SkinnableBackGroundHelper(view)
        flowHelper = SkinnableViewFlowHelper()
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        flowHelper.startSkinFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundTintHelper.updateSkin()
        }
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
    }
}