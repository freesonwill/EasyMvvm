package arch.cayenne.lib.skin.widget.helper

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

    /**
     *使用 findViewTreeLifecycleOwner，fragment 没有OnDestroyView时，就会及时监听换肤通知更新皮肤
     * 避免使用viewPager切fragment时换肤造成的闪烁
     * */
    fun startSkinFlow(scope: CoroutineScope?, updateSkin: (skinName: String) -> Unit) {
        if (skinFlowJob?.isActive == true) {
            return
        }
        skinFlowJob = scope?.launch(Dispatchers.IO) {
            sportSkinManager.skinFlow.collect {
                if (it == lastSkin) {
                    return@collect
                }
                launch(Dispatchers.Main) {
                    updateSkin.invoke(it)
                    lastSkin = it
                }

            }
        }
    }

    fun startLanguageFlow(updateLanguage: (local: Locale) -> Unit) {
        languageFlowJob?.cancel()
        languageFlowJob = CoroutineScope(Dispatchers.IO).launch {
            sportSkinManager.languageFlow.collect {
                it?.let {
                    launch(Dispatchers.Main) {
                        updateLanguage(it)
                    }
                }
            }
        }
    }

    fun destroyFlow() {
//        skinFlowJob?.cancel()
//        skinFlowJob = null
        languageFlowJob?.cancel()
        languageFlowJob = null
    }

}