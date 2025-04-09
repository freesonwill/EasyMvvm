package com.walisport.module.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.module.home.repository.HomeRepository
import galaxy.client.proto.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class HomeViewModel : BaseViewModel() {
    private val repository : HomeRepository by inject { parametersOf(viewModelScope) }

    fun initHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.getStatistical<Client.StatisticalResp>()

            when(res.error) {
                null -> {
                    val list = res.data?.statisticalList
                    list?.forEach {
                        "玩法类型 ${it.playName}".logd("HomeViewModel")
                    }
                }is ResponseTimeOutError -> {
                    res
                }

            }
        }
    }
}