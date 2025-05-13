package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes

interface SkinnableResourceLoader {

    fun getColor(context: Context, @ColorRes resId: Int): Int

    fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList?

    fun getDrawable(context: Context, @AnyRes resId: Int): Drawable?

    fun getTargetResourceId(context: Context, @AnyRes resId: Int): Int

    fun getSkinName(): String

    //给BuildInLoader设置 其他Loader没有效果
    fun setSecondarySkin(skinName: String)

}