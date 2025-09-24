package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.helper.SkinnableImageHelper

/**
 * @author: wenxi
 * @date: 11/9/25 10:54
 * @description:
 */
class SkinnableBizImageImpl(private val view: ImageView) : SkinnableBizBackgroundImpl(view),ISkinnableImageBiz {
    lateinit var imageHelper: SkinnableImageHelper private set

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super.initView(context, attrs, defStyleAttr)
        imageHelper = SkinnableImageHelper(view)
        imageHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setImageResource(resId: Int) {
        val nResId = flowHelper.checkOriginId(view.context,resId)
        imageHelper.setSrcId(nResId)
    }

    override fun getRadius(): Float {
        return imageHelper.getRadius()
    }

    override fun updateSkin(msgType: SkinMsgType) {
        super.updateSkin(msgType)
        imageHelper.updateSkin(msgType)
    }

}