package com.walisport.lib.skin

import android.content.Context
import com.walisport.lib.skin.res.SportSkinAssetsResourceLoader
import com.walisport.lib.skin.res.SportSkinBuildInResourceLoader
import com.walisport.lib.skin.res.SportSkinResourceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

class SportSkinManager {
    private val TAG: String = "sportmanager"
    private var languageCode: String = "en"
    val resourcesManager:SportSkinResourceManager by inject(SportSkinResourceManager::class.java)

    private val _skinFlow = MutableStateFlow("")
    private val _languageFlow = MutableStateFlow<Locale?>(null)
    val skinFlow: Flow<String> = _skinFlow
    val languageFlow: Flow<Locale?> = _languageFlow

    suspend fun loadSkin(skinName: String) {
        resourcesManager.initResource(SportSkinBuildInResourceLoader(skinName))
        _skinFlow.emit(skinName)
    }

    suspend fun loadSkinAsset(context: Context, skinName: String) {
        resourcesManager.initResource(SportSkinAssetsResourceLoader(context, skinName))
        _skinFlow.emit(skinName)
    }

    suspend fun restoreSkin() {
        resourcesManager.initResource(SportSkinBuildInResourceLoader(""))
        _skinFlow.emit("")
    }

    suspend fun changeLanguage(local:Locale) {
        _languageFlow.emit(local)
    }

}