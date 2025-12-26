package com.walisport.module.me.ui.viewmodel

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.GameBean
import com.walisport.module.business.common.data.GameContentData
import com.walisport.module.business.common.data.GamePageVo
import com.walisport.module.business.common.data.GameRecentRepository
import com.walisport.module.business.common.data.GameRecentRepository.Companion.INITIAL_PAGE
import com.walisport.module.business.common.data.constants.GameSortType
import com.walisport.module.business.common.data.toGameContentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class RecentlyTabViewModel : BaseViewModel() {

    private val repository: GameRecentRepository by inject { parametersOf(viewModelScope) }

    private val _gameClickData: MutableLiveData<GameBean?> = MutableLiveData()
    val gameClickData: LiveData<GameBean?> = _gameClickData

    private val _gameListLiveData: MutableLiveData<List<GameContentData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameContentData>> = _gameListLiveData

    private val _totalCountLiveData: MutableLiveData<Long> = MutableLiveData()
    val totalCountLiveData: LiveData<Long> = _totalCountLiveData

    private var page: Int = INITIAL_PAGE
    private var sortType: GameSortType = GameSortType.HOT
    private var category: Int = 0

    fun setCategory(category: Int) {
        this.category = category
    }

    //获取是否点击了游戏详情
    fun getIsClickGame(){
        viewModelScope.launch(Dispatchers.IO) {
            _gameClickData.postValue(repository.queryGameClick())
        }
    }

    fun setIsClickGame(flag:Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.setGameClick(flag)
        }
    }

    fun queryGameList() {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.queryGameList(page , sortType , emptyList() , category)
                } ,
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {
                        val gamePageVo = it.dataAs<GamePageVo>()
                        val totalItems = gamePageVo?.pagination?.totalItems
                        _totalCountLiveData.value = totalItems?.toLong() ?: 0L
                        val hasMore = gamePageVo?.pagination?.hasMore ?: false
                        val size = gamePageVo?.list?.size ?: 0
                        val isEmpty = size == 0
                        if (page == INITIAL_PAGE && isEmpty) {
                            setState(DataState.DataEmpty)
                        } else if (!hasMore) {   //如果hasMore为false，则表明列表已经加载到底部
                            setState(DataState.NoMoreData)
                            //给_gameListLiveData添加数据
                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = gamePageVo?.list?.map { gameVo ->
                                gameVo.toGameContentData(
                                    (page * 100 + gameVo.id).toLong() ,
                                    sortType
                                )
                            }
                            currentList.addAll(list ?: emptyList())
                            _gameListLiveData.value = currentList
                        } else {
                            setState(DataState.LoadSuccess)
                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = gamePageVo?.list?.map { gameVo ->
                                gameVo.toGameContentData(
                                    (page * 100 + gameVo.id).toLong() ,
                                    sortType
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
        queryGameList()
        _gameListLiveData.value = emptyList()
    }

    fun loadNextPage() {
        if (apiStateListener.value == DataState.LoadSuccess) {
            page++
            queryGameList()
        }
    }
}