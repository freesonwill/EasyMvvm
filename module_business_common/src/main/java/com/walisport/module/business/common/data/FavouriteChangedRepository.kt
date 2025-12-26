package com.walisport.module.business.common.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow

class FavouriteChangedRepository(
    override val scope: CoroutineScope,
) : BaseRepository() {
    private val favouriteChangedStateFlow = MutableSharedFlow<Boolean>()

    fun getFavouriteChangedFlow() = favouriteChangedStateFlow

    suspend fun notifyFavouriteChanged(isChanged: Boolean) {
        favouriteChangedStateFlow.emit(isChanged)
    }
}