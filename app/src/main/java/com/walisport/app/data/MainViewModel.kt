package com.walisport.app.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.remote.Response
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import com.walisport.lib_base.utils.LogUtilsExt.logd
import com.walisport.lib_base.utils.LogUtilsExt.logi
import kotlinx.coroutines.launch

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel(private val repo: MainRepository) : BaseViewModel() {

    fun startSocketConnect() {
        viewModelScope.launch {
            repo.startSocket().collect {
                "connect result $it".logi(this::class.java.simpleName)
            }
        }
    }

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