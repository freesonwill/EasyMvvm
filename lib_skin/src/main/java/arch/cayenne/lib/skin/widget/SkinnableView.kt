package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizBackgroundImpl

open class SkinnableView : View, ISkinnableBiz {

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

    override fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        biz = SkinnableBizBackgroundImpl(this)
        biz.initView(context, attrs, defStyleAttr)
    }

    override fun setBackgroundResource(resId: Int) {
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