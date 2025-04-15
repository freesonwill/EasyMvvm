package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.LeagueMatchBean

class LeagueViewModel : BaseViewModel() {

    private val _leagueData = MutableLiveData<LeagueMatchBean>()
    val leagueData: LiveData<LeagueMatchBean> get() = _leagueData


    fun setData(bean: LeagueMatchBean) {
        _leagueData.value = bean
    }
}