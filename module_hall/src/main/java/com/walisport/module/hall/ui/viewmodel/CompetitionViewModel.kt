package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.DailyBetMatchData
import com.walisport.module.hall.data.DailyBetMatchVo
import com.walisport.module.hall.data.DayPageVo
import com.walisport.module.hall.data.GameAllRankingToday
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.RankingRepository
import com.walisport.module.hall.data.toDailyBetMatchData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class CompetitionViewModel : BaseViewModel() {

    private val repository: RankingRepository by inject { parametersOf(viewModelScope) }

    private val _rankingListLiveData: MutableLiveData<List<GameAllRankingToday>> = MutableLiveData()
    val rankingListLiveData: LiveData<List<GameAllRankingToday>> = _rankingListLiveData

    private val _dailyMatchLiveData: MutableLiveData<DailyBetMatchData> = MutableLiveData()
    val dailyMatchLiveData: LiveData<DailyBetMatchData> = _dailyMatchLiveData

    private var page: Int = INITIAL_PAGE

    /**
     * 每日投注比赛信息
     */
    fun getDayMatchDetail() {
        viewModelScope.launch {
            callApi(
                {
                    repository.getDayMatchDetail()
                } ,
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            val dailyBetMatchVo = it.dataAs<DailyBetMatchVo>()

                            dailyBetMatchVo?.toDailyBetMatchData().let {
                                _dailyMatchLiveData.value = it
                            }

                        }

                        else -> {}
                    }
                } , autoUpdateState = false
            )
        }
    }


    /**
     * 每日比赛榜单
     */
    fun queryDailyMatchList() {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.getDailyMatch(page)
                } ,
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                            setState(DataState.NetworkUnavailable)
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            viewModelScope.launch(Dispatchers.IO) {
                                val bettingPageVo = it.dataAs<DayPageVo>()
                                val hasMore = bettingPageVo?.pagination?.hasMore ?: false
                                val size = bettingPageVo?.list?.size ?: 0
                                val isEmpty = size == 0
                                val list =
                                    bettingPageVo?.list?.map { repository.toGameAllRankingToday(it) }
                                        ?: emptyList()
                                launch(Dispatchers.Main) {
                                    when {
                                        page == INITIAL_PAGE && isEmpty -> setState(DataState.DataEmpty)
                                        !hasMore -> {
                                            setState(DataState.NoMoreData)
                                            _rankingListLiveData.value =
                                                (_rankingListLiveData.value ?: emptyList()) + list
                                        }

                                        else -> {
                                            setState(DataState.LoadSuccess)
                                            _rankingListLiveData.value =
                                                (_rankingListLiveData.value ?: emptyList()) + list
                                        }
                                    }
                                }

                            }

                        }

                        else -> {}
                    }
                } , autoUpdateState = false
            )
        }

    }

}