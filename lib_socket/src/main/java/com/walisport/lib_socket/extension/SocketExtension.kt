package com.walisport.lib_socket.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * collect once when get first Subscriber
 * **/
fun <T> MutableSharedFlow<T>.collectFirstSubscribe(action: suspend (() -> Unit)): MutableSharedFlow<T> {
    this.subscriptionCount
        .map { count -> count > 0 }
        .distinctUntilChanged()
        .onEach { isFirstSubscript ->
            if (isFirstSubscript) {
                action.invoke()
            }
        }
        .launchIn(CoroutineScope(Dispatchers.IO))
    return this
}