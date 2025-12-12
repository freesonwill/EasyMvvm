package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import com.walisport.module.hall.data.Avatar
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.GameVo
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HallRepository.Companion.DEFAULT_GAME_SIZE
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.data.constants.GameSortType
import com.walisport.module.hall.data.toGameContentData
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import kotlin.random.Random

@KoinViewModel
class GameContentViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    private val _gameListLiveData: MutableLiveData<List<GameContentData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameContentData>> = _gameListLiveData

    private var page: Int = INITIAL_PAGE
    private var sortType: GameSortType = GameSortType.HOT
    private var suppliers: List<Int> = emptyList()


    fun setSortType(sortType: GameSortType) {
        this.sortType = sortType
    }

    fun queryGameList() {
        viewModelScope.launch {
            setState(DataState.Loading)
            callApi(
                {
                    repository.queryGameList(page , sortType , suppliers)
                } ,
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {

                        val size = it.dataAs<List<GameVo>>()?.size ?: 0
                        val isEmpty = size == 0
                        if (page == INITIAL_PAGE && isEmpty) {
                            setState(DataState.DataEmpty)
                        } else if (size < DEFAULT_GAME_SIZE) {   //如果返回成功，但是数据size小于10，则表明列表已经加载到底部
                            setState(DataState.NoMoreData)
                            //给_gameListLiveData添加数据
                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = it.dataAs<List<GameVo>>()?.map { gameVo ->
                                gameVo.toGameContentData(
                                    (page * 100 + gameVo.id).toLong() ,
                                    sortType
                                )
                            }
                            currentList.addAll(list?: emptyList())
                            _gameListLiveData.value = currentList
                        } else {
                            setState(DataState.LoadSuccess)

                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = it.dataAs<List<GameVo>>()?.map { gameVo ->
                                gameVo.toGameContentData(
                                    (page * 100 + gameVo.id).toLong() ,
                                    sortType
                                )
                            }
                            currentList.addAll(list?: emptyList())
                            _gameListLiveData.value = currentList
                        }

                    }
                } , autoUpdateState = false
            )
        }
    }

    fun applySorting() {
        page = INITIAL_PAGE
        _gameListLiveData.value= emptyList()
        queryGameList()
    }

    fun loadNextPage() {
        if (apiStateListener.value == DataState.LoadSuccess) {
            page++
            queryGameList()
        }

    }

    companion object {
        const val INITIAL_PAGE = 1
    }
}