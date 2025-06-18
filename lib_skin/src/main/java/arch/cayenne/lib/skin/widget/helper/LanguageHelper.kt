package arch.cayenne.lib.skin.widget.helper

import android.content.Context
import android.view.View
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import java.util.Locale

/**
 * @author: wenxi
 * @date: 16/6/25 17:10
 * @description:
 */
abstract class LanguageHelper(mView: View) : SkinnableHelper(mView) {
    protected var stringContext: Context? = null //更新语言
    private var lastLanguage: Locale? = null

    /**
     * 更新语言时，更新context的configuration local
     * */
    fun refreshContext(locale: Locale?) {
        if (lastLanguage == locale) {
            return
        }
        lastLanguage = locale
        val configuration = mView.context.resources.configuration
        configuration.setLocale(locale)
        stringContext = mView.context.applicationContext.createConfigurationContext(configuration)
    }

    /**
     * 刷新language
     * */
    abstract fun updateLanguage(locale: Locale)

}