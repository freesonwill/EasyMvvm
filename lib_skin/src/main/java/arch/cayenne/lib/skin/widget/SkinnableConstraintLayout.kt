package arch.cayenne.lib.skin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper

//2131362349
open class SkinnableConstraintLayout : ConstraintLayout {
    private lateinit var backGroundHelper: SkinnableBackGroundHelper
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

    @SuppressLint("SuspiciousIndentation")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        flowHelper.startSkinFlow {
            backGroundHelper.updateSkin()
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backGroundHelper = SkinnableBackGroundHelper(this)
        backGroundHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun onDetachedFromWindow() {
        flowHelper.destroyFlow()
        super.onDetachedFromWindow()
    }

}
