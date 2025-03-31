package com.walisport.app.repo

import com.walisport.lib_base.data.repository.BaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class FloatingButtonRepository: BaseRepository() {

    // TODO 測試用，須改成監聽下注list size
    fun getBettingCount(): Flow<Int> {
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
}