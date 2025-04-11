package com.walisport.app.data

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import com.walisport.lib.common.ui.viewmodel.BaseActivityViewModel
import com.walisport.lib_socket.data.ConnectState
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.lib_socket.data.SocketResponseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SplashViewModel : BaseActivityViewModel() {

    val homeTimeSeconds: MutableLiveData<Int> = MutableLiveData()
    private val repository: SplashRepository by inject { parametersOf(viewModelScope) }

    val jumpToMainOrLogin = MediatorLiveData<Boolean>().apply {
        addSource(homeTimeSeconds) {
            if (it == 0) {
                value = loginIsSuccess.value ?: false
            }
        }
        addSource(loginIsSuccess) {
            if (homeTimeSeconds.value == 0) {
                value = it
            }
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

}