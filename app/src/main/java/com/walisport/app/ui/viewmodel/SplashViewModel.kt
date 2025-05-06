package com.walisport.app.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.skin.SportSkinManager
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import com.walisport.app.data.repo.SplashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SplashViewModel : BaseActivityViewModel() {

    val homeTimeSeconds: MutableLiveData<Int> = MutableLiveData()
    private val repository: SplashRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SportSkinManager by inject { parametersOf(viewModelScope) }
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

    //加载皮肤方案
    fun loadMyAppSkin() {
        viewModelScope.launch {
            val skinType = repository.getSkinType()
            skinManager.loadSkin(skinType)
        }
    }

}