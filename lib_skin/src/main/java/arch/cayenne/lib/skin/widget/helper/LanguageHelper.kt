package arch.cayenne.lib.skin.widget.helper

import android.view.View
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.data.SkinMsgType
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

/**
 * @author: wenxi
 * @date: 16/6/25 17:10
 * @description:
 */
 interface LanguageHelper {

    /**
     * 刷新language
     * */
    abstract fun updateLanguage(locale: Locale)

}