package arch.cayenne.lib.common.utils

import android.content.Context
import android.os.Build


/**
 *
 * @date: 2025/11/13 15:46
 * @description:
 */
object LanguageUtils {

    fun isChinese(context: Context): Boolean {
        val locale =
            context.resources.configuration.locales[0]

        val language = locale.language
        val country = locale.country // 可选：区分简繁

        // 简体中文 (zh_CN), 繁体中文 (zh_TW, zh_HK)
        return "zh" == language
        // 更精确判断：
        // return "zh".equals(language) && ("CN".equals(country) || "TW".equals(country) || "HK".equals(country));
    }
}