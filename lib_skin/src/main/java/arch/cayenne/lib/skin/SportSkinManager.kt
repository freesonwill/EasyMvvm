package arch.cayenne.lib.skin

import android.content.Context
import arch.cayenne.lib.skin.res.SportSkinAssetsResourceLoader
import arch.cayenne.lib.skin.res.SportSkinBuildInResourceLoader
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

class SportSkinManager {
    private val resourcesManager = SportSkinResourceManager
    private val _skinFlow = MutableStateFlow("")
    private val _languageFlow = MutableStateFlow<Locale?>(null)
    val skinFlow: Flow<String> = _skinFlow
    val languageFlow: Flow<Locale?> = _languageFlow

    /**
     * 加载后缀名皮肤
     * */
    suspend fun loadSkin(skinName: String) {
        resourcesManager.initResource(SportSkinBuildInResourceLoader(skinName))
        _skinFlow.emit(skinName)
    }

    /**
     *加载皮肤包
     * */
    suspend fun loadSkinAsset(context: Context, skinName: String) {
        resourcesManager.initResource(SportSkinAssetsResourceLoader(context, skinName))
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
        resourcesManager.initResource(SportSkinBuildInResourceLoader(""))
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
     *切换语言
     * */
    suspend fun changeLanguage(local: Locale) {
        _languageFlow.emit(local)
    }

    /**
     * 获取皮肤名称
     * */
    fun getSkinName() = resourcesManager.getSkinName()

}