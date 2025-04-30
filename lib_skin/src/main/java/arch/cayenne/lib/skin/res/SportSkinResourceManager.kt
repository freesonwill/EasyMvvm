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
 object SportSkinResourceManager {
    private var resourceLoader: SportSkinResourceLoader = SportSkinBuildInResourceLoader("")

    fun initResource(resourceLoader: SportSkinResourceLoader) {
        SportSkinResourceManager.resourceLoader = resourceLoader
        if(resourceLoader is SportSkinBuildInResourceLoader){
            resourceLoader.getSkinName()
        }
    }
    fun setSecondaryName(secondaryName:String){
        resourceLoader.setSecondarySkin(secondaryName)
    }

    fun restoreSecondaryName(){
        resourceLoader.setSecondarySkin("")
    }

    fun getTextResourceText(
        context: Context,
        @StringRes resId: Int,
        languageCode: String = "en"
    ): String {
        val configuration = Configuration(context.resources.configuration)
        val locale = configuration.locale
        if (locale.language != languageCode) {
            val metrics = context.resources.displayMetrics
            val configuration = Configuration(context.resources.configuration)
            configuration.setLocale(Locale(languageCode))
            val localizeContext = context.createConfigurationContext(configuration)
            context.resources.updateConfiguration(configuration, metrics)
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
}