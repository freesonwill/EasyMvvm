package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SkinnableRelativeLayout:RelativeLayout {
    private val backgroundTintHelper = SkinnableBackGroundHelper(this)
    private val flowHelper = SkinnableViewFlowHelper()

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow {
            backgroundTintHelper.updateSkin()
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }
}