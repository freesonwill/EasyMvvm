package arch.cayenne.lib.skin.widget.biz

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * @author: wenxi
 * @date: 5/9/25 17:30
 * @description:
 */
interface ISkinnableTextBiz : ISkinnableBiz {

    fun setTextAppearance(resId: Int)

    fun setTextAppearance(context: Context, resId: Int)

    fun setTextRes(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray())

    fun setHintRes(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray())

    fun setTextColorRes(@ColorRes color: Int)

    fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int,
        @DrawableRes top: Int,
        @DrawableRes end: Int,
        @DrawableRes bottom: Int
    )

    fun setCompoundDrawablesWithIntrinsicBounds(
        @DrawableRes left: Int,
        @DrawableRes top: Int,
        @DrawableRes right: Int,
        @DrawableRes bottom: Int
    )

}