package com.walisport.module.bet.repo

import com.walisport.lib_base.data.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class FloatingButtonRepository: BaseRepository() {

    // TODO 測試用，須改成監聽下注list size
    fun observeBettingCount(): Flow<Int> {
        var count = 0
        return MutableSharedFlow<Int>().apply {
            scope.launch {
                while (true) {
                    delay(3_000L)
                    emit(++count)
                }
            }
        }
    }

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
}