package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.setting.data.NotifyMatchType
import com.walisport.module.setting.data.SettingBean
import com.walisport.module.setting.data.SettingRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SettingViewModel : BaseViewModel() {

    private val repository: SettingRepository by inject { parametersOf(viewModelScope) }

    private val _systemSetting = MutableLiveData<SettingBean?>()
    val systemSetting: LiveData<SettingBean?> get() = _systemSetting

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
        repository.setLanguageType(type)
    }

    //获取语言类型
    fun getLanguageType(): String {
        return repository.getLanguageType()
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

    //获取系统配置
    fun getSystemSetting() {
        viewModelScope.launch {
            val resp = repository.getSystemSetting()
            val data = resp?.let {
                SettingBean(
                    oddType = it.oddType,
                    systemGoal = NotifyMatchType(
                        it.systemGoal.betMatch,
                        it.systemGoal.collectMatch,
                        it.systemGoal.allMatch
                    ),
                    systemKickOff = NotifyMatchType(
                        it.systemKickOff.betMatch,
                        it.systemKickOff.collectMatch,
                        it.systemKickOff.allMatch
                    ),
                    appGoal = NotifyMatchType(
                        it.appGoal.betMatch,
                        it.appGoal.collectMatch,
                        it.appGoal.allMatch
                    ),
                    background = it.background,
                    lang = it.lang
                )
            }
            _systemSetting.value = data
        }
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
}