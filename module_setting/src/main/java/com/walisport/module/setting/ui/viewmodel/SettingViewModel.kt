package com.walisport.module.setting.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.setting.R
import com.walisport.module.setting.data.OddsDisplayEnum
import com.walisport.module.setting.data.SettingRepository
import galaxy.common.proto.Common.Setting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class SettingViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    private val _skinType = MutableLiveData<String>()
    val skinType: LiveData<String> = _skinType

    private val _displayType = MutableLiveData<OddsDisplayEnum>()
    val displayType: LiveData<OddsDisplayEnum> get() = _displayType

    init {
        viewModelScope.launch {
            launch {
                _displayType.value = repository.getOddsType()
            }
            launch(Dispatchers.IO) {
                repository.observerOddsDisplay.collect {
                    val type = OddsDisplayEnum.entries[it]
                    _displayType.postValue(type)
                }
            }
        }
    }

    //设置语言类型
    fun setLanguageType(type: String) {
        viewModelScope.launch {
            repository.setLanguageType(type)
            languageManager.changeLanguage(Locale(type))
            val setting = getSystemSetting()
            repository.updateSettingReq(setting)
            _language.value = type
        }
    }

    //获取语言类型
    fun getLanguageType(): String {
        return repository.getLanguageType()
    }

    //获取皮肤背景
    fun getSkinType(): String {
        return repository.getSkinType()
    }

    //设置皮肤背景，只换肤不写入记录，写入记录得调用setSkinRecord
    fun setSkinType(type: String) {
        viewModelScope.launch {
            val logicSkin = getLogicSkinType(type)
            skinManager.loadSkin(logicSkin)
            _skinType.value = type
        }
    }

    //点击确认按钮后才会写入数据，否则只是换肤显示
    fun setSkinRecord(type: String) {
        repository.setSkinType(type)
    }

    //设置系统通知-进球
    fun setSystemGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        repository.setSystemGoal(bet, fav, all);
    }

    //设置系统通知-开赛
    fun setKickGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        repository.setKickGoal(bet, fav, all);
    }

    //设置应用内通知-进球
    fun setAppGoal(bet: Boolean, fav: Boolean, all: Boolean) {
        repository.setAppGoal(bet, fav, all);
    }

    fun getSystemBet(): Boolean {
        return repository.getSystemBet()
    }

    fun getSystemFav(): Boolean {
        return repository.getSystemFav()
    }

    fun getSystemAll(): Boolean {
        return repository.getSystemAll()
    }

    fun getKickBet(): Boolean {
        return repository.getKickBet()
    }

    fun getKickFav(): Boolean {
        return repository.getKickFav()
    }

    fun getKickAll(): Boolean {
        return repository.getKickAll()
    }

    fun getAppBet(): Boolean {
        return repository.getAppBet()
    }

    fun getAppFav(): Boolean {
        return repository.getAppFav()
    }

    fun getAppAll(): Boolean {
        return repository.getAppAll()
    }

    fun getSkinnableLanguage(context: Context): String {
        val lang = repository.getLanguageType()
        return when (lang) {
            LanguageType.LANGUAGE_ENGLISH.value -> SkinnableResourceManager.getString(
                context,
                R.string.menu_language_english,
                languageManager.getLanguage()
            )

            LanguageType.LANGUAGE_PT.value -> SkinnableResourceManager.getString(
                context,
                R.string.menu_language_portugal,
                languageManager.getLanguage()
            )

            LanguageType.LANGUAGE_ID.value -> SkinnableResourceManager.getString(
                context,
                R.string.menu_language_indonesia,
                languageManager.getLanguage()
            )

            else -> SkinnableResourceManager.getString(
                context,
                R.string.menu_language_simple,
                languageManager.getLanguage()
            )
        }
    }

    private fun getSystemSetting(): Setting {
        val language = getLanguageType()
        return Setting.newBuilder().apply {
            lang = when (language) {
                LanguageType.LANGUAGE_ENGLISH.value -> "en-US"
                LanguageType.LANGUAGE_ID.value -> "id-ID"
                LanguageType.LANGUAGE_PT.value -> "pt-PT"
                else -> "zh-CN"
            }
        }.build()
    }

    //UI界面上有6种主题，但是逻辑上暂时就白蓝和经典两种
    private fun getLogicSkinType(skinType: String): String {
        return when (skinType) {
            SkinType.SKIN_WHITE_BLUE.value -> SkinType.SKIN_WHITE_BLUE.value
            SkinType.SKIN_WHITE_GREEN.value -> SkinType.SKIN_WHITE_BLUE.value
            else -> SkinType.SKIN_CLASSIC.value
        }
    }

    fun getLanguage() = languageManager.getLanguage()
}