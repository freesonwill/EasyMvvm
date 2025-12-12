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
import arch.cayenne.lib.common.ui.viewmodel.Event
import com.walisport.module.hall.data.Avatar
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HotColdType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import kotlin.random.Random
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import com.walisport.module.hall.data.GameCategoryVo
import com.walisport.module.hall.data.GamePageVo
import com.walisport.module.hall.data.HallRepository.Companion.INITIAL_PAGE
import com.walisport.module.hall.data.constants.GameSortType
import com.walisport.module.hall.data.toGameContentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@KoinViewModel
class GameContentViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }
    private val _gameListLiveData: MutableLiveData<List<GameContentData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<GameContentData>> = _gameListLiveData

    private var page: Int = INITIAL_PAGE
    private var sortType: GameSortType = GameSortType.HOT
    private var suppliers: List<Int> = emptyList()
    private var category: Int = 0


    private val _gameSupplierList = UnPeekLiveData<List<GameSupplierDataModel>>()
    val gameSupplierList: UnPeekLiveData<List<GameSupplierDataModel>> = _gameSupplierList


    private val _gameCategoryList = UnPeekLiveData<List<GameCategoryVo>>()
    val gameCategoryList: UnPeekLiveData<List<GameCategoryVo>> = _gameCategoryList

    // 保存彈窗中的選中狀態（跨彈窗生命週期）
    private val _savedTournamentSelections = MutableLiveData<List<Int>>(
        emptyList()
    )
    val savedTournamentSelections: LiveData<List<Int>> = _savedTournamentSelections

    // 通知按鈕選中狀態變化
    private val _buttonHasSelection = UnPeekLiveData<Boolean>()
    val buttonHasSelection: UnPeekLiveData<Boolean> = _buttonHasSelection

    fun getSuppliers(type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeSupplierByGameTypeId(type)
                .collect {
                    launch(Dispatchers.Main) {
                        _gameSupplierList.value = it
                    }
                }
        }

    }

    fun setCategory(category: Int) {
        this.category = category
    }

    fun getCategory() : Int{
       return category
    }


    fun setSupplier(suppliers: List<Int>) {
        this.suppliers = suppliers
    }

    fun setSortType(sortType: GameSortType) {
        this.sortType = sortType
    }


    fun queryGameList() {
        viewModelScope.launch {
            setState(DataState.Loading)
            callApi(
                {
                    repository.queryGameList(page , sortType , suppliers , category)
                } ,
                {
                    if (it is ApiResponseState.Failed) {
                        setState(DataState.NetworkUnavailable)
                    } else if (it is ApiResponseState.Succeeded<*>) {

                        val gamePageVo = it.dataAs<GamePageVo>()
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
        _gameListLiveData.value = emptyList()
        queryGameList()
    }

    fun loadNextPage() {
        if (apiStateListener.value == DataState.LoadSuccess) {
            page++
            queryGameList()
        }

    }

    // 通知需要清除 tlLeagueList 的選中狀態
    private val _shouldClearLeagueListSelection = MutableLiveData<Event<Unit>>()
    val shouldClearLeagueListSelection: LiveData<Event<Unit>> = _shouldClearLeagueListSelection

    // 保存選中狀態（在確認時調用）
    fun saveTournamentSelections(selections: List<Int>) {
        LogUtils.e("updateTournamentButtonStyle--------->${selections},${category},${this}")
        _savedTournamentSelections.value = selections
        // 通知按鈕狀態更新
        _buttonHasSelection.value = selections.isNotEmpty()
        // TODO: 未來同時保存到後端
    }

    // 觸發清除 tlLeagueList 選中狀態（在彈窗確認時調用）
    fun requestClearLeagueListSelection() {
        _shouldClearLeagueListSelection.value = Event(Unit)
    }


}