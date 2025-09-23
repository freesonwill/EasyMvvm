package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

/**
 * @date: 2025/7/30 15:55
 * @description:换肤业务接口
 */
interface ISkinnableBiz {
    fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
    fun setBackgroundResource(@DrawableRes resId: Int)
    fun setTintColorRes(@ColorRes resId: Int)
    fun setForegroundRes(@AnyRes resId: Int)
    fun forceUpdateSkin()
}