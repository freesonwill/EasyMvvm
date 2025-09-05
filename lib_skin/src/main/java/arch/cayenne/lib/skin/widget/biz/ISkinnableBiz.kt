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
    fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
    fun updateBackground(@DrawableRes resId: Int)
    fun updateBackgroundTintId(@ColorRes resId: Int)
    fun updateForegroundId(@AnyRes resId: Int)
}