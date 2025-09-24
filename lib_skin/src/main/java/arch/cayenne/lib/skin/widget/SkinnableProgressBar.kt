package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizBackgroundImpl

class SkinnableProgressBar : ProgressBar, ISkinnableBiz {

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

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }

    override fun setTintColorRes(resId: Int) {
        biz.setTintColorRes(resId)
    }

    override fun setForegroundRes(resId: Int) {
        biz.setForegroundRes(resId)
    }

    override fun updateSkin(msgType: SkinMsgType) {
        biz.updateSkin(msgType)
    }
}