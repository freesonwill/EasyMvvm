package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatCheckBox
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.ISkinnableTextBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizTextImpl

class SkinnableCheckBox : AppCompatCheckBox, ISkinnableBiz {
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

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
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
        biz.setBackgroundResource(resId)
    }

    override fun setTintColorRes(@ColorRes resId: Int) {
        biz.setTintColorRes(resId)
    }

    override fun setForegroundRes(@AnyRes resId: Int) {
        biz.setForegroundRes(resId)
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun updateSkin(msgType: SkinMsgType) {
        biz.updateSkin(msgType)
    }

}