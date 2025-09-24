package arch.cayenne.lib.skin.widget.helper

import arch.cayenne.lib.skin.LanguageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

/**
 * @date: 2025/9/23 15:32
 * @description: language辅助类
 */
class SkinnableLanguageFlowHelper {
    private var languageFlowJob: Job? = null
    private val languageManager: LanguageManager by inject(LanguageManager::class.java)

    fun startLanguageFlow(scope: CoroutineScope?, updateLanguage: (local: Locale) -> Unit) {
        languageFlowJob?.cancel()
        languageFlowJob = scope?.launch {
            languageManager.languageFlow.collect {
                it?.let {
                    updateLanguage(it)
                }
            }
        }
    }

    fun destroyFlow() {
        languageFlowJob?.cancel()
        languageFlowJob = null
    }
}