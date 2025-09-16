package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizBackgroundImpl
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SkinnableViewFlowHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

open class SkinnableView : View {

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

    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        biz.updateBackground(resId)
    }

    fun setTintColorRes(@ColorRes resId: Int) {
        biz.updateBackgroundTintId(resId)
    }

    fun setForegroundRes(@AnyRes resId: Int) {
        biz.updateForegroundId(resId)
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

}