package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableImageHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

/**
 * @author: wenxi
 * @date: 11/9/25 10:54
 * @description:
 */
class SkinnableBizImageImpl(private val view: ImageView) : ISkinnableImageBiz {
    lateinit var backgroundHelper: SkinnableBackGroundHelper private set
    lateinit var flowHelper: SkinnableViewFlowHelper private set
    lateinit var imageHelper: SkinnableImageHelper private set

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        flowHelper = SkinnableViewFlowHelper()
        backgroundHelper = SkinnableBackGroundHelper(view)
        imageHelper = SkinnableImageHelper(view)
        backgroundHelper.loadFromAttributes(attrs, defStyleAttr)
        imageHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        flowHelper.startSkinFlow(view.findViewTreeLifecycleOwner()?.lifecycleScope) {
            backgroundHelper.updateSkin()
            imageHelper.updateSkin()
        }
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
    }

    override fun updateBackground(resId: Int) {
        backgroundHelper.updateBackground(resId)
    }

    override fun updateBackgroundTintId(resId: Int) {
        backgroundHelper.updateBackgroundTintId(resId)
    }

    override fun updateForegroundId(resId: Int) {
        backgroundHelper.updateForegroundId(resId)
    }

    override fun setImageResource(resId: Int) {
        imageHelper.setSrcId(resId)
    }

    override fun getRadius(): Float {
        return imageHelper.getRadius()
    }

}