package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SkinnableRecyclerView : RecyclerView {
    private val flowHelper = SkinnableViewFlowHelper()


    private val backgroundHelper = SkinnableBackGroundHelper(this)

    constructor(context: Context) : super(context){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context,attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context,attrs,defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow {
            backgroundHelper.updateSkin()
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        backgroundHelper.setSrcId(resId)
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }
}