package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.DayPageVo
import com.walisport.module.hall.data.DayVo
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.RankingRepository
import com.walisport.module.hall.data.toGameAllRankingListData
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class DailyMatchViewModel : BaseViewModel() {
    private val repository: RankingRepository by inject { parametersOf(viewModelScope) }

    private val _gameListLiveData: MutableLiveData<List<GameAllRankingListData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameAllRankingListData>> = _gameListLiveData

    private var page: Int = INITIAL_PAGE

    fun getDayMatchDetail() {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.getDayMatchDetail()
                } ,
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                            setState(DataState.NetworkUnavailable)
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            setState(DataState.LoadSuccess)
                        }

                        else -> {}
                    }
                } , autoUpdateState = false
            )
        }
    }


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
                            val bettingPageVo = it.dataAs<DayPageVo>()
                            val hasMore = bettingPageVo?.pagination?.hasMore ?: false
                            val size = bettingPageVo?.list?.size ?: 0
                            val isEmpty = size == 0

                            val list =
                                bettingPageVo?.list?.map { it.toGameAllRankingListData() }
                                    ?: emptyList()
                            when {
                                page == INITIAL_PAGE && isEmpty -> setState(DataState.DataEmpty)
                                !hasMore -> {
                                    setState(DataState.NoMoreData)
                                    _gameListLiveData.value =
                                        (_gameListLiveData.value ?: emptyList()) + list
                                }

                                else -> {
                                    setState(DataState.LoadSuccess)
                                    _gameListLiveData.value =
                                        (_gameListLiveData.value ?: emptyList()) + list
                                }
                            }
                        }

                        else -> {}
                    }
                } , autoUpdateState = false
            )
        }

    }

    fun loadNextPage() {
        if (apiStateListener.value == DataState.LoadSuccess) {
            page++
            queryDailyMatchList()
        }
    }

    companion object {
        const val DELAY: Long = 30_000
    }


}


