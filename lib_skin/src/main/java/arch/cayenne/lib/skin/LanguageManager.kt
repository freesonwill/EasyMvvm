package arch.cayenne.lib.skin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

/**
 * @author: wenxi
 * @date: 16/6/25 15:59
 * @description: 切换语言
 */
class LanguageManager {
    private val _languageFlow = MutableStateFlow<Locale?>(null)
    val languageFlow: Flow<Locale?> = _languageFlow

    /**
     *切换语言
     * */
    suspend fun changeLanguage(local: Locale) {
        _languageFlow.emit(local)
    }

    /**
     * 获取当前语言
     * */
    fun getLanguage():Locale?{
        return _languageFlow.value
    }
}