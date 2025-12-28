package com.walisport.module.me.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.business.common.data.FavouriteChangedRepository
import com.walisport.module.business.common.data.GameContentData
import com.walisport.module.business.common.data.GameFavouriteRepository
import com.walisport.module.business.common.data.GameFavouriteRepository.Companion.INITIAL_PAGE
import com.walisport.module.business.common.data.ProfilePlayedPageVo
import com.walisport.module.business.common.data.toGameContentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class GameCollectionTabViewModel : BaseViewModel() {

    private val repository: GameFavouriteRepository by inject { parametersOf(viewModelScope) }
    private val favouriteChangedRepository: FavouriteChangedRepository by inject {
        parametersOf(
            viewModelScope
        )
    }

    private val _gameListLiveData: MutableLiveData<List<GameContentData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameContentData>> = _gameListLiveData

    private val _favouriteChangedLiveData = MutableLiveData<Boolean>()
    val favouriteChangedLiveData: LiveData<Boolean> get() = _favouriteChangedLiveData

    private val _totalCountLiveData = MutableLiveData<Long>()
    val totalCountLiveData: LiveData<Long> get() = _totalCountLiveData


    private var page: Int = INITIAL_PAGE


    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            launch {
                favouriteChangedRepository.getFavouriteChangedFlow().collect {
                    _favouriteChangedLiveData.postValue(it)
                }
            }
        }

    }

    private fun getGameCollectList() {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.getGameCollectList(page)
                },
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {

                        val profilePlayedPageVo = it.dataAs<ProfilePlayedPageVo>()
                        val hasMore = profilePlayedPageVo?.pagination?.hasMore ?: false
                        val totalItems = profilePlayedPageVo?.pagination?.totalItems
                        _totalCountLiveData.value = totalItems?.toLong() ?: 0L
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
                                try {
                                    gameVo.toGameContentData()
                                } catch (e: Exception) {
                                    null
                                }
                            }?.filterNotNull()
                            currentList.addAll(list ?: emptyList())
                            _gameListLiveData.value = currentList
                        } else {
                            setState(DataState.LoadSuccess)
                            val currentList =
                                _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                            val list = profilePlayedPageVo?.list?.map { gameVo ->
                                try {
                                    gameVo.toGameContentData()
                                } catch (e: Exception) {
                                    null
                                }
                            }?.filterNotNull()
                            currentList.addAll(list ?: emptyList())
                            _gameListLiveData.value = currentList
                        }


                    }
                }, autoUpdateState = false
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

    fun setIsClickGame(flag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setGameClick(flag)
        }
    }


}