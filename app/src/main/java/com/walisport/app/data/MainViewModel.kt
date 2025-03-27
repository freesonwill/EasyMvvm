package com.walisport.app.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import com.walisport.lib_base.utils.LogUtilsExt.loge
import com.walisport.lib_base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ConnectSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel(private val repo: MainRepository) : BaseViewModel() {
    fun startSocketConnectAndLogin(
        uid: Int,
        token: String
    ) {
        //第一次與socket連接，成功後做登入，如果每次斷線重連後都需要登入，可以把登入寫進observe內
        viewModelScope.launch {
            when(val connectState = repo.startSocket()) {
                null -> { //timeout
                    "Connection Timeout".loge(MainViewModel::class.java.simpleName)
                }
                is ConnectSuccess -> {  //連接成功
                    "Connection Success".logi(MainViewModel::class.java.simpleName)
                    login(uid, token)
                }
                else -> {   //連接不成功
                    "Connection Failure -> $connectState".loge(MainViewModel::class.java.simpleName)
                }
            }
        }

    }

    // 此為範例
    fun login(
        uid: Int,
        token: String
    ) {
        repo.sendLogin(uid, token)
    }
}