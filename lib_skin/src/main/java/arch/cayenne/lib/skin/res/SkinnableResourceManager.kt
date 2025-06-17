package arch.cayenne.lib.skin.res

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.annotation.AnyRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import java.util.Locale

/**
 * 获取对应资源文件
 * */
object SkinnableResourceManager {
    private var resourceLoader: SkinnableResourceLoader = SkinnableBuildInResourceLoader("")
    private var currentLanguage: Locale? = null

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

   internal fun getTextResourceText(
        context: Context,
        @StringRes resId: Int,
        locale: Locale?
    ): String {
        val locale = context.resources.configuration.locale
        if (locale.language != languageCode) {
            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(Locale(languageCode))
            val localizeContext = context.createConfigurationContext(configuration)
            return localizeContext.resources.getString(resId)
        }
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

    fun getString(context: Context,@StringRes resId:Int):String{
        return getTextResourceText(context,resId, currentLanguage)
    }
}