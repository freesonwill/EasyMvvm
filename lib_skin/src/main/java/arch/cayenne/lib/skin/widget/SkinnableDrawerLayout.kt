package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.drawerlayout.widget.DrawerLayout
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.lib.skin.widget.biz.SkinnableBizBackgroundImpl

/**
 * @date: 2025/9/19 19:27
 * @description:
 */
open class SkinnableDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DrawerLayout(context, attrs, defStyleAttr) {
    private lateinit var biz: ISkinnableBiz

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        biz = SkinnableBizBackgroundImpl(this)
        biz.initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        biz.onAttachedToWindow()
    }


    override fun setBackgroundResource(resId: Int) {
        super.setBackgroundResource(resId)
        biz.updateBackground(resId)
    }

    fun setTintColorRes(@ColorRes resId: Int){
        biz.updateBackgroundTintId(resId)
    }

    fun setForegroundRes(@AnyRes resId: Int){
        biz.updateForegroundId(resId)
    }

    override fun onDetachedFromWindow() {
        biz.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }
}