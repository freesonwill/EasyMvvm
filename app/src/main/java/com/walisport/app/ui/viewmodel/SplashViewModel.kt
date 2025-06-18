package com.walisport.app.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import arch.cayenne.lib.skin.LanguageManager
import com.walisport.app.data.repo.SplashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.util.Locale

class SplashViewModel : BaseActivityViewModel() {

    val homeTimeSeconds: MutableLiveData<Int> = MutableLiveData()
    private val repository: SplashRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val languageManager:LanguageManager by inject { parametersOf(viewModelScope) }

    val jumpToMainOrLogin = MediatorLiveData<Boolean>().apply {
        addSource(homeTimeSeconds) {
            if (it == 0) {
                value = loginIsSuccess.value ?: false
            }
        }
        addSource(loginIsSuccess) {
            value = it
        }
    }

    init {
        viewModelScope.launch {
            repository.countDownSecondsLD.collect {
                homeTimeSeconds.value = it
            }
        }
    }

    fun saveUserData(uid: Int, token: String) {
        repository.saveUserData(uid, token)
    }

    fun connectToServer() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startSocket()
        }
    }

    //加载皮肤和语言方案
    fun loadMyAppSkin() {
        viewModelScope.launch {
            val skinType = repository.getSkinType()
            val logicType = getLogicSkinType(skinType)
            skinManager.loadSkin(logicType)
            val langType = repository.getLanguageType()
            languageManager.changeLanguage(Locale(langType))
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