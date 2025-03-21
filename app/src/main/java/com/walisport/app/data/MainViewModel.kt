package com.walisport.app.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.remote.Response
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel(private val repo: MainRepository) : BaseViewModel() {

    // 此為範例
    fun login() {
        viewModelScope.launch {
            sendApi({
                repo.login()
            }, {
                if (it is Response.Success) {
                    // TODO 成功
                } else if (it is Response.Failed) {
                    // TODO 失敗
                }
            })
        }
    }
}