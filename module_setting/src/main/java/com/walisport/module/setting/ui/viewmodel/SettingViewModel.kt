package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import com.walisport.module.setting.data.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SettingViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }

    private val _language = MutableLiveData<LanguageType>()
    val language: LiveData<LanguageType> get() =  _language

    private val _skinType = MutableLiveData<String>()
    val skinType: LiveData<String> = _skinType

    private val _displayType = MutableLiveData<OddsDisplayEnum>()
    val displayType: LiveData<OddsDisplayEnum> get() = _displayType

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                repository.observerOddsDisplay.onStart {
                    _displayType.postValue(repository.getOddsType())
                }.collect {
                    repository.observerOddsDisplay.collect {
                        val type = OddsDisplayEnum.entries[it]
                        _displayType.postValue(type)
                    }
                }
            }
            launch(Dispatchers.IO) {
                repository.observerLanguage.onStart {
                    _language.postValue(repository.getLanguageType())
                }.collect {
                    repository.observerLanguage.collect { lang ->
                        _language.postValue(LanguageType.findLanguage(lang))
                    }
                }
            }
        }
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