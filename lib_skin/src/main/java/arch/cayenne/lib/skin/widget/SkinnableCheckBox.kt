package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatCheckBox
import arch.cayenne.lib.skin.widget.biz.ISkinnableTextBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizTextImpl

class SkinnableCheckBox : AppCompatCheckBox, ISkinnable {
    private lateinit var biz: ISkinnableTextBiz

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
        biz = SkinnableBizTextImpl(this)
        biz.initView(context, attrs, defStyleAttr)
    }

    fun setTextRes(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()) {
        biz.setTextRes(stringRes, *formatArgs)
    }

    fun setHintRes(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()) {
        biz.setHintRes(stringRes, *formatArgs)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
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

    override fun forceUpdateSkin() {
        biz.forceUpdateSkin()
    }

}