package arch.cayenne.lib.skin

import android.content.Context
import arch.cayenne.lib.skin.res.SkinnableAssetsResourceLoader
import arch.cayenne.lib.skin.res.SkinnableBuildInResourceLoader
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

class SkinnableManager {
    private val resourcesManager = SkinnableResourceManager
    private val _skinFlow = MutableStateFlow("")
    val skinFlow: Flow<String> = _skinFlow
    private var skinNameArray:List<String>? = null

    /**
     * 加载后缀名皮肤
     * */
    suspend fun loadSkin(skinName: String) {
        resourcesManager.initResource(SkinnableBuildInResourceLoader(skinName))
        _skinFlow.emit(skinName)
    }

    /**
     *加载皮肤包
     * */
    suspend fun loadSkinAsset(context: Context, skinName: String) {
        resourcesManager.initResource(SkinnableAssetsResourceLoader(context, skinName))
        _skinFlow.emit(skinName)
    }

    /**
     *设置临时皮肤名称
     * 使用后为了效率尽快调用restoreTmpSkin重置临时皮肤
     * */
    suspend fun loadTmpSkin(tmpSkinName: String) {
        resourcesManager.setSecondaryName(tmpSkinName)
        _skinFlow.emit(tmpSkinName)
    }

    /**
     *使用默认皮肤
     * */
    suspend fun restoreSkin() {
        resourcesManager.initResource(SkinnableBuildInResourceLoader(""))
        _skinFlow.emit("")
    }


    /**
     *临时皮肤名称重置
     * */
    suspend fun restoreTmpSkin() {
        resourcesManager.restoreSecondaryName()
        _skinFlow.emit("")
    }


    /**
     * 获取皮肤名称
     * */
    fun getSkinName() = resourcesManager.getSkinName()

    /**
     * 由于在setBackgroundResource setImageResource时，部分代码传入了带有后缀名的resourceId resid_light
     * 导致在换肤时资源id寻找错误，对传入的resourceId添加了后缀 resid_light_dark,因此传入所有有用的皮肤名称，
     * 在setBackgroundResource setImageResource时过滤掉带有后缀的resourceId 示例：_light 传入light
     * */
    fun fitterSuffixSkin(vararg skins:String = emptyArray()){
        if(skins.isNotEmpty()){
            skinNameArray = skins.asList()
        }
    }

    fun getSkinNameArray():List<String>?{
        return skinNameArray
    }

}