package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import android.util.AttributeSet

/**
 * @date: 2025/7/30 15:55
 * @description:换肤业务接口
 */
interface ISkinnableBiz {
    fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
}