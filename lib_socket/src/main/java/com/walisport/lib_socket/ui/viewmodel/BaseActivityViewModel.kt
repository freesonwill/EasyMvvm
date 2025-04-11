package com.walisport.lib_socket.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ConnectState
import com.walisport.lib_socket.data.SocketResponseError
import com.walisport.lib_socket.repository.ConnectingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * 這個ViewModel主要處理一些共通監聽的是像，例如統一監聽斷線後重新連線與登入狀態
 * 現在需要做全背景監聽的只有SplashActivity和AppNavActivity
 * */
abstract class BaseActivityViewModel : BaseViewModel() {
    private val connectingRepository: ConnectingRepository by inject { parametersOf(viewModelScope) }
    // 每個activity針對登入和離線錯誤都有不同的處理，接收到相對應的livedata後各自處理
    val loginIsSuccess = MutableLiveData<Boolean>()
    val connectingError = MutableLiveData<SocketResponseError>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            connectingRepository.getConnectStateFlow().collect { connectState ->
                when(connectState) {
                    is ConnectState.ConnectSuccess -> {
                        "Connection Success".logi(BaseActivityViewModel::class.java.simpleName)
                        login()
                    }
                    else -> {   //收到這錯誤，可以根據需求處理，SocketManager會啟動自動重連機制
                        "Connection Failure -> $connectState".loge(BaseActivityViewModel::class.java.simpleName)
                    }
                }
            }
        }

    }
    //當連線成功時，自動地去做補登入
    private fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = connectingRepository.sendLogin()
            withContext(Dispatchers.Main) {
                when(result.error) {
                    null -> {
                        "Login  Is Success = ${result.data?.success}".logi(this@BaseActivityViewModel::class.java.simpleName)
                        loginIsSuccess.value = result.data?.success == true
                    }
                    else -> {   //其餘錯誤
                        connectingError.value = result.error!!
                    }
                }
            }
        }
    }
    override fun reset() {
        connectingRepository.reset()
    }
}