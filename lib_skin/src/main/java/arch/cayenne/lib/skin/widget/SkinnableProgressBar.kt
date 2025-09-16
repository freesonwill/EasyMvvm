package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizBackgroundImpl
import arch.cayenne.lib.skin.widget.helper.SkinnableProgressBarHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SkinnableProgressBar : ProgressBar {

    private lateinit var biz: ISkinnableBiz

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
        biz.onAttachedToWindow()
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        biz = SkinnableBizBackgroundImpl(this)
        biz.initView(context, attrs, defStyleAttr)
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }
}