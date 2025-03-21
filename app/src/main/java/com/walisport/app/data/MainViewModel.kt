package com.walisport.app.data

import androidx.lifecycle.viewModelScope
import com.walisport.lib_base.data.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel(val repo: MainRepository) : BaseViewModel() {

    override fun onInit() {
        super.onInit()
        viewModelScope.launch {
            repo.startSocket()
        }
    }
}