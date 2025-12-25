package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.business.common.data.GameContentData
import com.walisport.module.hall.data.GameFavouriteRepository
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.ProfilePlayedPageVo
import com.walisport.module.hall.data.toGameContentData
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class GameFavouriteViewModel : BaseViewModel() {

    private val repository: GameFavouriteRepository by inject { parametersOf(viewModelScope) }
    private val _gameListLiveData: MutableLiveData<List<GameContentData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameContentData>> = _gameListLiveData

    private var page: Int = INITIAL_PAGE

    private fun getGameCollectList() {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.getGameCollectList(page )
                } ,
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {

                        val profilePlayedPageVo = it.dataAs<ProfilePlayedPageVo>()
                        val hasMore = profilePlayedPageVo?.pagination?.hasMore ?: false
                        val size = profilePlayedPageVo?.list?.size ?: 0
                        val isEmpty = size == 0
                        if (page == INITIAL_PAGE && isEmpty) {
                            setState(DataState.DataEmpty)
                        } else if (!hasMore) {   //如果hasMore为false，则表明列表已经加载到底部
                            setState(DataState.NoMoreData)
                            //给_gameListLiveData添加数据
                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = profilePlayedPageVo?.list?.map { gameVo ->
                                gameVo.toGameContentData(
                                    (page * 100 + gameVo.gameType).toLong() ,
                                )
                            }
                            currentList.addAll(list ?: emptyList())
                            _gameListLiveData.value = currentList
                        } else {
                            setState(DataState.LoadSuccess)
                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = profilePlayedPageVo?.list?.map { gameVo ->
                                gameVo.toGameContentData(
                                    (page * 100 + gameVo.gameType).toLong() ,
                                )
                            }
                            currentList.addAll(list ?: emptyList())
                            _gameListLiveData.value = currentList
                        }

                    }
                } , autoUpdateState = false
            )
        }
    }

    fun reload() {
        page = INITIAL_PAGE
        getGameCollectList()
        _gameListLiveData.value = emptyList()
    }

    fun loadNextPage() {
        if (apiStateListener.value == DataState.LoadSuccess) {
            page++
            getGameCollectList()
        }
    }


}