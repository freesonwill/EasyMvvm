package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import java.util.Locale

/**
 * 获取对应资源文件
 * */
object SkinnableResourceManager {
    private var resourceLoader: SkinnableResourceLoader = SkinnableBuildInResourceLoader("")

    fun initResource(resourceLoader: SkinnableResourceLoader) {
        SkinnableResourceManager.resourceLoader = resourceLoader
        if (resourceLoader is SkinnableBuildInResourceLoader) {
            resourceLoader.getSkinName()
        }
    }

    fun setSecondaryName(secondaryName: String) {
        resourceLoader.setSecondarySkin(secondaryName)
    }

    fun restoreSecondaryName() {
        resourceLoader.setSecondarySkin("")
    }

    fun getTextResourceText(
        context: Context,
        @StringRes resId: Int,
    ): String {
        return context.resources.getString(resId)
    }

    fun getColor(context: Context, @ColorRes resId: Int): Int =
        resourceLoader.getColor(context, resId)

    fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList? =
        resourceLoader.getColorStateList(context, resId)

    fun getDrawable(context: Context, @AnyRes resId: Int): Drawable? =
        resourceLoader.getDrawable(context, resId)

    fun getTargetResourceId(context: Context, @AnyRes resId: Int): Int =
        resourceLoader.getTargetResourceId(context, resId)

    fun getSkinName() = resourceLoader.getSkinName()
}