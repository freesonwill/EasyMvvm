package arch.cayenne.lib.skin.widget.helper

import android.annotation.SuppressLint
import android.content.Context
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

class SkinnableViewFlowHelper {
    private var skinFlowJob: Job? = null
    private var languageFlowJob: Job? = null
    private val skinManager: SkinnableManager by inject(SkinnableManager::class.java)
    private val languageManager: LanguageManager by inject(LanguageManager::class.java)
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
            skinManager.skinFlow.collect {
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
            languageManager.languageFlow.collect {
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

    /**
     * 检查传入的resourceId是否带了皮肤名，如果带了皮肤名就返回原id
     * */
    @SuppressLint("SuspiciousIndentation")
    fun checkOriginId(context: Context, resId: Int): Int {
        val resName = context.resources.getResourceEntryName(resId)
        val id = skinManager.getSkinNameArray()?.let { skinNames ->
            if (skinNames.isEmpty()) return@let resId
            val suffix = skinNames.find { resName.endsWith(it) }
            return@let suffix?.let {
                val originName = resName.replace("_$suffix", "")
                val originId =
                    SkinnableResourceManager.getOriginResourceId(context, originName, resId)
                return originId
            } ?: resId
        } ?: resId
        return id
    }

}