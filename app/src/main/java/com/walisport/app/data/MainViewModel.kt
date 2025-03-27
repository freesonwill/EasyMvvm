package com.walisport.app.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import com.walisport.lib_base.utils.LogUtilsExt.loge
import com.walisport.lib_base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ConnectSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel(private val repo: MainRepository) : BaseViewModel() {

}