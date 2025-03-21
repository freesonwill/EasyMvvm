package com.walisport.lib_base.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.walisport.lib_base.data.remote.ApiResponseState
import com.walisport.lib_base.data.remote.Response

/**
 * @author: zhangsan
 * @date: 2025/3/14 09:51
 * @description:
 */
abstract class BaseViewModel : ViewModel() {

    private val _onApiResponseStateListener = MutableLiveData<ApiResponseState>(ApiResponseState.Idle)
    val onApiResponseStateListener: LiveData<ApiResponseState> = _onApiResponseStateListener

    protected suspend fun sendApi(request: suspend () -> Response, callback: (Response) -> Unit) {
        _onApiResponseStateListener.value = ApiResponseState.Processing
        val response = request()
        callback(response)
        if (response is Response.Success) {
            _onApiResponseStateListener.value = ApiResponseState.Succeeded
        } else if (response is Response.Failed) {
            _onApiResponseStateListener.value = ApiResponseState.Failed(response.code, response.desc)
        }
        resetApiResponseState()
    }

    protected fun resetApiResponseState() {
        _onApiResponseStateListener.value = ApiResponseState.Idle
    }
}