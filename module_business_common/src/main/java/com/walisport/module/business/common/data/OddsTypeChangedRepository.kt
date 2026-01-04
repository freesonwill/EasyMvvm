package com.walisport.module.business.common.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow

class OddsTypeChangedRepository(
    override val scope: CoroutineScope,
) : BaseRepository() {
    private val oddsTypeChangedStateFlow = MutableSharedFlow<Int>()

    fun getOddsTypeChangedFlow() = oddsTypeChangedStateFlow

    suspend fun notifyOddsTypeChanged(oddsDisplayType: Int) {
        oddsTypeChangedStateFlow.emit(oddsDisplayType)
    }
}