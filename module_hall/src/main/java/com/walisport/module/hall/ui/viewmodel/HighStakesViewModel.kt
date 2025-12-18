package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.collectIn
import com.walisport.module.hall.data.BigPageVo
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.RankingRepository
import com.walisport.module.hall.data.constants.toGameAllRankingListData
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class HighStakesViewModel : BaseViewModel() {
    private val repository: RankingRepository by inject { parametersOf(viewModelScope) }

    private val _gameListLiveData: MutableLiveData<List<GameAllRankingListData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameAllRankingListData>> = _gameListLiveData

    private var page: Int = INITIAL_PAGE

    fun queryRecordBig(){
        setState(DataState.Loading)
        kotlinx.coroutines.flow.flow {
            while (true) {
                emit(repository.recordBig(page))
                kotlinx.coroutines.delay(DELAY)
            }
        }.collectIn(viewModelScope) { result ->
            when (result) {
                is ApiResponseState.Failed -> {
                    setState(DataState.NetworkUnavailable)
                }

                is ApiResponseState.Succeeded<*> -> {
                    val bigPageVo = result.dataAs<BigPageVo>()
                    val hasMore = bigPageVo?.pagination?.hasMore ?: false
                    val size = bigPageVo?.list?.size ?: 0
                    val isEmpty = size == 0

                    val list = bigPageVo?.list?.map { it.toGameAllRankingListData() } ?: emptyList()
                    when {
                        page == INITIAL_PAGE && isEmpty -> setState(DataState.DataEmpty)
                        !hasMore -> {
                            setState(DataState.NoMoreData)
                            _gameListLiveData.value = list
                        }
                        else -> {
                            setState(DataState.LoadSuccess)
                            _gameListLiveData.value = list
                        }
                    }

                }

                else -> {}
            }
        }
    }

    companion object {
        const val DELAY: Long = 30_000
    }


}