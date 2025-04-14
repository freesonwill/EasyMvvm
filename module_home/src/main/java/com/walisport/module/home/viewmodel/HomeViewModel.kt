package com.walisport.module.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.base.utils.LogUtilsExt.loge
import com.walisport.lib.base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.module.home.data.PlayType
import com.walisport.module.home.repository.HomeRepository
import galaxy.client.proto.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class HomeViewModel : BaseViewModel() {
    private val repository : HomeRepository by inject { parametersOf(viewModelScope) }
    var currentPlayType : PlayType = PlayType.Today
    var currentSport: Int = 2

    fun initHomeData() {
        getAllSportStatistical()
    }

    //取得上方三個Tab(今日、早盤、冠軍)內運動的數量
    private fun getAllSportStatistical() {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = repository.getAllStatistical()
            if (!isSuccess) {
                "get sport list from api failed!!".loge(HomeViewModel::class.java.simpleName)
            } else {
                //取得目前這個Tab的第一個運動，設為預設選取並接著取得該運動的聯賽資料
                currentSport = repository.getDefaultSport(currentPlayType.id)
                getAllTournaments()
            }
        }
    }

    fun getCurrentSportStatistical() {
        //這個list可以用來處理球種的是否反灰
        val list = repository.getSportStatistical(currentPlayType.id)
    }

    //取得聯賽資料
    private fun getAllTournaments() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTournaments(currentPlayType.id, currentSport)

        }
    }

}