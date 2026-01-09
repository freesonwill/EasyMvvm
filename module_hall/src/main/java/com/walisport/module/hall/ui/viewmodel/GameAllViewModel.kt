package com.walisport.module.hall.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.business.common.data.Category
import com.walisport.module.business.common.data.GamePageVo
import com.walisport.module.business.common.data.toGameContentData
import com.walisport.module.business.common.ui.viewmodel.BaseBannerViewModel
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllContentData
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HallRepository.Companion.DEFAULT_GAME_SIZE
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class GameAllViewModel : BaseBannerViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    private val _gameRecentList = UnPeekLiveData<List<GameAllContentData>>()
    val gameRecentList: UnPeekLiveData<List<GameAllContentData>> = _gameRecentList

    private var _gameListLiveData: MutableLiveData<List<GameAllContentData>> = MutableLiveData()
    var gameListLiveData: LiveData<List<GameAllContentData>> = _gameListLiveData

    fun setIsClickGame(flag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setGameClick(flag)
        }
    }

    fun getGameTitleSize(): Int {
        return _gameRecentList.value?.size ?: 0
    }

    fun getGameCategory(position: Int): Int {
        return _gameRecentList.value?.get(position)?.category ?: -1
    }

    fun getGame(position: Int): GameAllContentData? {
        return _gameRecentList.value?.get(position)
    }

    @SuppressLint("SuspiciousIndentation")
    fun queryGameList(category: Int, data: GameAllContentData) {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.queryAllGameList(INITIAL_PAGE, DEFAULT_GAME_SIZE, category)
                },
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {
                        val gamePageVo = it.dataAs<GamePageVo>()
                        setState(DataState.LoadSuccess)
                        val currentList =
                            _gameListLiveData.value?.toMutableList() ?: mutableListOf()
                        val list = gamePageVo?.list?.map { gameVo ->
                            gameVo.toGameContentData(
                            )
                        }
                        data.gameList = list!!
                        if (data.gameList.isNotEmpty()){
                            currentList.add(data)
                        }
                        _gameListLiveData.postValue(currentList)
                    }
                }, autoUpdateState = false
            )
        }
    }

    fun getAllList() {

        val mockData = listOf(
            GameAllContentData(
                name = R.string.hot_game.getString(),
                category = 0, emptyList()
            ),

            GameAllContentData(
                name = R.string.tab_avava_original.getString(),
                category = Category.ORIGIN.type, emptyList()
            ),

            GameAllContentData(
                name = R.string.tab_fishing.getString(),
                category = Category.FISH.type, emptyList()
            ),

            GameAllContentData(
                name = R.string.tab_real.getString(),
                category = Category.VIDEO.type, emptyList()
            ),

            GameAllContentData(
                name = R.string.tab_table.getString(),
                category = Category.POKER.type, emptyList()
            ),

            GameAllContentData(
                name = R.string.tab_esprots.getString(),
                category = Category.TIGER.type, emptyList()
            ),

            GameAllContentData(
                name = R.string.tab_wls.getString(),
                category = Category.SPORT.type, emptyList()
            ),
            GameAllContentData(
                name = R.string.tab_lottery.getString(),
                category = Category.LOTTERY.type, emptyList()
            ),
            GameAllContentData(
                name = R.string.tab_esprots.getString(),
                category = Category.ELECTRONIC.type, emptyList()
            )
        )
        _gameRecentList.postValue(mockData)

    }

}