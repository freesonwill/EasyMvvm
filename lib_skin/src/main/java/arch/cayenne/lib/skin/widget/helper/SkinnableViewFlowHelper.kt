package arch.cayenne.lib.skin.widget.helper

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.skin.SkinnableManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

class SkinnableViewFlowHelper {
    private var skinFlowJob: Job? = null
    private var languageFlowJob: Job? = null
    private val sportSkinManager: SkinnableManager by inject(SkinnableManager::class.java)
    private val TAG = this@SkinnableViewFlowHelper::class.java.simpleName
    private var lastSkin: String = ""

    fun startSkinFlow(updateSkin: (skinName: String) -> Unit) {
        skinFlowJob?.cancel()
        skinFlowJob = CoroutineScope(Dispatchers.Main).launch {
            sportSkinManager.skinFlow.collect {
                if (it == lastSkin) {
                    return@collect
                }
                updateSkin.invoke(it)
                lastSkin = it
            }
        }
    }

    fun startLanguageFlow(updateLanguage: (local: Locale) -> Unit) {
        languageFlowJob?.cancel()
        languageFlowJob = CoroutineScope(Dispatchers.Main).launch {
            sportSkinManager.languageFlow.collect {
                it?.let {
                    updateLanguage(it)
                } ?: "updateLanguage failed: local is null".loge(TAG)
            }
        }
    }

    fun destroyFlow() {
        skinFlowJob?.cancel()
        languageFlowJob?.cancel()
    }

}