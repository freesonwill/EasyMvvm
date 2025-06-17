package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.SkinnableManager
import com.walisport.module.setting.data.SettingRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class SettingViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val languageManager:LanguageManager by inject { parametersOf(viewModelScope)  }

    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    private val _skinType = MutableLiveData<String>()
    val skinType: LiveData<String> = _skinType

    //设置赔率方式
    fun setOddsType(type: Int) {
        repository.setOddsType(type)
    }

    //获取赔率显示方式
    fun getOddsType(): Int {
        return repository.getOddsType()
    }

    //设置语言类型
    fun setLanguageType(type: String) {
        viewModelScope.launch {
            repository.setLanguageType(type)
            languageManager.changeLanguage(Locale(type))
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

    //获取系统通知-进球选项是全部还是部分
    fun getSystemAllOrPart(): Boolean {
        val bet = repository.getSystemBet()
        val fav = repository.getSystemFav()
        val all = repository.getSystemAll()
        return bet && fav && all
    }

    //获取系统通知-开赛选项是全部还是部分
    fun getKickAllOrPart(): Boolean {
        val bet = repository.getKickBet()
        val fav = repository.getKickFav()
        val all = repository.getKickAll()
        return bet && fav && all
    }

    //获取应用内通知-进球选项是全部还是部分
    fun getAppAllOrPart(): Boolean {
        val bet = repository.getAppBet()
        val fav = repository.getAppFav()
        val all = repository.getAppAll()
        return bet && fav && all
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

    //调用接口设置赔率方式
    fun updateOddsSetting(type: Int) {
        viewModelScope.launch {
            val req = Common.Setting.newBuilder().apply {
                oddType = type
            }.build()
            repository.updateSettingReq(req)
        }
    }

    //UI界面上有6种主题，但是逻辑上暂时就白蓝和经典两种
    private fun getLogicSkinType(skinType: String): String {
        return when (skinType) {
            SkinType.SKIN_WHITE_BLUE.value -> SkinType.SKIN_WHITE_BLUE.value
            SkinType.SKIN_WHITE_GREEN.value -> SkinType.SKIN_WHITE_BLUE.value
            else -> SkinType.SKIN_CLASSIC.value
        }
    }
}