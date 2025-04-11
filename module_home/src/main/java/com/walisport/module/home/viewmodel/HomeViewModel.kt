package com.walisport.module.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.base.utils.LogUtilsExt.loge
import com.walisport.lib.base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.module.home.repository.HomeRepository
import galaxy.client.proto.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.lang.ref.Cleaner

class HomeViewModel : BaseViewModel() {
    private val repository : HomeRepository by inject { parametersOf(viewModelScope) }

    fun initHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = repository.getStatistical()
            if (!isSuccess) {
                "get sport list from api failed!!".loge(HomeViewModel::class.java.simpleName)
            }
        }
    }
}