package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.CompetitionBean
import com.walisport.module.live.data.model.TableBean
import com.walisport.module.live.data.model.TeamStats

class LiveStandingsViewModel : BaseViewModel() {

    private val _competitionBean = MutableLiveData<CompetitionBean>()
    val competitionBean: LiveData<CompetitionBean> = _competitionBean

    //添加积分榜数据
    fun addCompetitionLiveData() {
        val stats1 = TeamStats(1, 1, 1, 1, 2, 3, 1, 1, "巴西", "")
        val stats2 = TeamStats(2, 3, 2, 2, 4, 2, 2, 2, "厄瓜多尔", "")
        val stats3 = TeamStats(3, 3, 3, 0, 2, 1, 3, 1, "荷兰", "")
        val stats4 = TeamStats(4, 5, 4, 1, 1, 0, 4, 0, "阿根廷", "")
        val sList = listOf(stats1, stats2, stats3, stats4)
        val table0 = TableBean(0, "世界杯", 1, 1, sList)
        val table1 = TableBean(2, "世界杯", 1, 1, sList)
        val table2 = TableBean(2, "世界杯", 2, 1, sList)
        val table3 = TableBean(3, "世界杯", 3, 1, sList)
        val table4 = TableBean(4, "世界杯", 4, 1, sList)
        val list = listOf(table0, table1, table2, table3, table4)
        val data = CompetitionBean(list)
        _competitionBean.value = data
    }
}