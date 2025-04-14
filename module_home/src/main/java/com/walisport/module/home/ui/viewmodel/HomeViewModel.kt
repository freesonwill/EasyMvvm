package com.walisport.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.home.enums.LeagueType

class HomeViewModel : BaseViewModel() {
    //待串接修改
    val selectedLeagueId = MutableLiveData<Int>().apply { value = LeagueType.ALL.leagueId }
    val selectedDate = MutableLiveData<String>().apply { value = "" } // 預設空 = 全部

    fun updateFilter(leagueId: Int, date: String) {
        selectedLeagueId.value = leagueId
        selectedDate.value = date
    }
}