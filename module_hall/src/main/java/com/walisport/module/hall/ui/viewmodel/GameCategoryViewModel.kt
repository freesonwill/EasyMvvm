package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.constants.GameSortType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class GameCategoryViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }
    val gameList = repository.gameListLiveData

    private var page: Int = 0
    private var sortType: GameSortType = GameSortType.HOT

    fun setSortType(sortType: GameSortType) {
        this.sortType = sortType
    }

    fun queryGameList() {
        repository.queryGameList(page , sortType)
    }

     fun applySorting() {
        page = 0
        repository.queryGameList(page , sortType)
    }

    fun loadNextPage() {
        page++
        repository.queryGameList(page , sortType)
    }
}