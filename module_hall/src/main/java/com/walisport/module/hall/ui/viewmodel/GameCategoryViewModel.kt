package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.Avatar
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HotColdType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import kotlin.random.Random

@KoinViewModel
class GameCategoryViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }
    val gameList = repository.gameListLiveData

    fun queryGameList(page:Int) {
        repository.queryGameList(page  )
    }
}