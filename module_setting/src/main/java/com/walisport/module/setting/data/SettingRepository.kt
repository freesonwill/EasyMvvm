package com.walisport.module.setting.data

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.common.data.UserDataKey
import com.walisport.lib.common.data.UserDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.java.KoinJavaComponent.inject

class SettingRepository(override val scope: CoroutineScope) : BaseRepository() {

    private val manager: UserDataManager by inject(UserDataManager::class.java)
    private val _skinType = MutableStateFlow<String?>(null)
    val skinType: StateFlow<String?>
        get() = _skinType

    //设置皮肤背景
    fun setSkinType(type: String) {
        manager.setKeyValue(UserDataKey.KEY_SKIN, type)
        _skinType.tryEmit(type)
    }

    //获取皮肤背景
    fun getSkinType(): String {
        return manager.getStringValue(UserDataKey.KEY_SKIN, "CLASSIC")
    }

    //设置赔率显示方式
    fun setOddsDisplayType(type: String) {
        manager.setKeyValue(UserDataKey.KEY_DISPLAY, type)
    }

    //获取赔率显示方式
    fun getOddsDisplayType(): String {
        return manager.getStringValue(UserDataKey.KEY_DISPLAY, "EP")
    }

    //设置语言类型
    fun setLanguageType(type: String) {
        manager.setKeyValue(UserDataKey.KEY_LANGUAGE, type)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return manager.getStringValue(UserDataKey.KEY_LANGUAGE, "SIMPLE")
    }
}