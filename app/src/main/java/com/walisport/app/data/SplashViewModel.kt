package com.walisport.app.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.base.utils.LogUtilsExt.loge
import com.walisport.lib.base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ConnectState
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.lib_socket.data.SocketResponseError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class SplashViewModel : BaseViewModel() {

    val homeTimeSeconds: MutableLiveData<Int> = MutableLiveData()
    private val repository: SplashRepository by inject { parametersOf(viewModelScope) }

    init {
        viewModelScope.launch {
            repository.countDownSecondsLD.collect {
                homeTimeSeconds.value = it
            }
        }
    }

    fun startSocketConnectAndLogin(
        uid: Int,
        token: String
    ) {
        //第一次與socket連接，成功後做登入，如果每次斷線重連後都需要登入，可以把登入寫進observe內
        viewModelScope.launch(Dispatchers.IO) {
            when(val connectState = repository.startSocket()) {
                is ConnectState.ConnectSuccess -> {  //連接成功
                    "Connection Success".logi(MainViewModel::class.java.simpleName)
                    login(uid, token)
                }
                else -> {   //連接不成功
                    "Connection Failure -> $connectState".loge(MainViewModel::class.java.simpleName)
                }
            }
        }

    }

    private fun login(
        uid: Int,
        token: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.sendLogin(uid, token)
            when(res.error) {
                null -> {
                    res.data?.apply {
                        "login isSuccess = ${this.success}".logi(this@SplashViewModel::class.java.simpleName)
                    }
                }
                is ResponseTimeOutError -> {
                    "login time out".loge(this@SplashViewModel::class.java.simpleName)
                }
                is SocketResponseError -> {
                    res.error!!.msg.loge(this@SplashViewModel::class.java.simpleName)
                }
            }
        }
    }

}