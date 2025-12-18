package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.BettingPageVo
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.RankingRepository
import com.walisport.module.hall.data.toGameAllRankingListData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class LatestBetViewModel : BaseViewModel() {
    private val repository: RankingRepository by inject { parametersOf(viewModelScope) }

    private val _gameListLiveData: MutableLiveData<List<GameAllRankingListData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameAllRankingListData>> = _gameListLiveData

    private var page: Int = INITIAL_PAGE


    fun queryLatestBetList() {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.recordBetting(page)
                } ,
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {

                        val bettingPageVo = it.dataAs<BettingPageVo>()
                        val hasMore = bettingPageVo?.pagination?.hasMore ?: false
                        val size = bettingPageVo?.list?.size ?: 0
                        val isEmpty = size == 0
                        if (page == INITIAL_PAGE && isEmpty) {
                            setState(DataState.DataEmpty)
                        } else if (!hasMore) {   //如果hasMore为false，则表明列表已经加载到底部
                            setState(DataState.NoMoreData)
                            val list = bettingPageVo?.list?.map { bettingVo ->
                                bettingVo.toGameAllRankingListData()
                            }
                            //只需拉一页，不能添加到现有的列表
                            _gameListLiveData.value = list ?: emptyList()
                        } else {
                            setState(DataState.LoadSuccess)
                            val list = bettingPageVo?.list?.map { gameVo ->
                                gameVo.toGameAllRankingListData()
                            }
                            //只需拉一页，不能添加到现有的列表
                            _gameListLiveData.value = list ?: emptyList()
                        }

                    }
                } , autoUpdateState = false
            )

            //每30秒重新拉取数据
            delay(DELAY)
            queryLatestBetList()
        }
    }

    companion object {
        const val DELAY: Long = 5_000
    }
}