package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.model.GoalTrendBean
import com.walisport.module.live.data.model.MatchEventBean
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveOutsViewModel : BaseViewModel() {

    private val repository: LiveMainRepository by inject { parametersOf(viewModelScope) }

    //直播详情页的赛况tab下的比赛事件列表数据
    private val _matchEventList = MutableLiveData<List<MatchEventBean>>()
    val matchEventList: LiveData<List<MatchEventBean>> = _matchEventList

    private val _goalTrendList = MutableLiveData<List<GoalTrendBean>>()
    val goalTrendList: LiveData<List<GoalTrendBean>> = _goalTrendList

    //暂时先写入进攻趋势的测试数据
    fun setGoalTrendData() {
        val array = ArrayList<GoalTrendBean>()
        val temp1 = GoalTrendBean(0, 10000101, true, 1, 2, 70)
        val temp2 = GoalTrendBean(0, 10000101, false, 2, 4, 60)
        val temp3 = GoalTrendBean(0, 10000101, true, 3, 10, 20)
        val temp4 = GoalTrendBean(0, 10000101, false, 3, 15, 10)
        val temp5 = GoalTrendBean(0, 10000101, true, 4, 20, 100)
        val temp6 = GoalTrendBean(0, 10000101, false, 1, 22, 70)
        val temp7 = GoalTrendBean(0, 10000101, false, 2, 26, 60)
        val temp8 = GoalTrendBean(0, 10000101, true, 4, 30, 20)
        val temp9 = GoalTrendBean(0, 10000101, false, 3, 40, 100)
        val temp10 = GoalTrendBean(0, 10000101, false, 3, 75, 80)
        array.add(temp1)
        array.add(temp2)
        array.add(temp3)
        array.add(temp4)
        array.add(temp5)
        array.add(temp6)
        array.add(temp7)
        array.add(temp8)
        array.add(temp9)
        array.add(temp10)
        _goalTrendList.value = array
    }


    //暂时先写入球赛事件的测试数据
    fun setMatchEventData() {
        val testData = ArrayList<MatchEventBean>()
        val tmp0 = MatchEventBean(1, 0, 0, "", 0, "", 0, "", 0, "")
        val tmp1 = MatchEventBean(1, 8, 1, "塞萨尔", 3, "雨果", 0, "", 0, "")
        val tmp2 = MatchEventBean(1, 22, 1, "塞萨尔", 2, "雨果", 2, "劳塔罗", 0, "")
        val tmp3 = MatchEventBean(1, 36, 0, "", 0, "", 1, "利桑德罗·马丁内斯", 3, "劳塔罗")
        val tmp4 = MatchEventBean(1, 40, 4, "塞萨尔", 2, "雨果", 0, "", 0, "")
        val tmp5 = MatchEventBean(1, 50, 1, "塞萨尔", 2, "雨果", 0, "", 0, "")
        val tmp6 = MatchEventBean(1, 55, 2, "塞萨尔", 2, "雨果", 0, "", 0, "")
        val tmp7 = MatchEventBean(1, 60, 0, "", 0, "", 3, "利桑德罗·马丁内斯", 3, "塞萨尔")
        val tmp8 = MatchEventBean(1, 75, 4, "塞萨尔", 2, "雨果", 0, "", 0, "")
        val tmp9 = MatchEventBean(1, 90, 1, "塞萨尔", 2, "雨果", 0, "", 0, "")
        val tmp10 = MatchEventBean(10, 0, 0, "", 0, "", 0, "", 0, "")
        testData.add(tmp0)
        testData.add(tmp1)
        testData.add(tmp2)
        testData.add(tmp3)
        testData.add(tmp4)
        testData.add(tmp5)
        testData.add(tmp6)
        testData.add(tmp7)
        testData.add(tmp8)
        testData.add(tmp9)
        testData.add(tmp10)
        _matchEventList.value = testData
    }
}