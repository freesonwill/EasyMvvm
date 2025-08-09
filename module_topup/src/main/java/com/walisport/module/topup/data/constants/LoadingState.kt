package com.walisport.module.topup.data.constants

import arch.cayenne.lib.base.data.constants.DataState

/**
 *
 * @date: 2025/8/8 17:02
 * @description:
 */
sealed class LoadingState:DataState {
    data object Loading : LoadingState()
    data object LoadSuccess : LoadingState()
    data object Refreshing : LoadingState()
    data object LoadingNext : LoadingState()

    data object DataEmpty : LoadingState()
}