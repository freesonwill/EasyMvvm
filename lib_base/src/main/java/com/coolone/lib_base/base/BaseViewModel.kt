package com.coolone.lib_base.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolone.lib_base.http.ApiException
import com.coolone.lib_base.inner.IViewModel
import com.xcjh.base_lib.callback.livedata.event.EventLiveData
import kotlinx.coroutines.launch

/**
 * Description:
 * author       : baoyuedong
 * email        : baoyuedong@tsenf.io
 * createTime   : 2023/9/1 11:32
 **/
typealias VmError = (e: ApiException) -> Unit

open class BaseViewModel : ViewModel(), IViewModel {
    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    /**
     * 内置封装好的可通知Activity/fragment 显示隐藏加载框 因为需要跟网络请求显示隐藏loading配套才加的，不然我加他个鸡儿加
     */
    inner class UiLoadingChange {
        //显示加载框
        val showDialog by lazy { EventLiveData<String>() }
        //隐藏
        val dismissDialog by lazy { EventLiveData<Boolean>() }
    }


    /**
     * 发起网络请求
     */
    protected fun <T> launch(block: () -> T, error: VmError? = null) {
        viewModelScope.launch {
            runCatching {
                block()
            }.onFailure {
                ApiException.getApiException(it).apply {
                    error?.invoke(this)
                }
            }
        }
    }

}