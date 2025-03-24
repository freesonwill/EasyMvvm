package com.walisport.lib_socket

import kotlinx.coroutines.flow.SharedFlow

interface ISocket<REQ, RES, State> {

    suspend fun connect(host: String): SharedFlow<State>

    fun disConnect()

    fun reConnect()

    fun send(data : REQ)

    //socket收到的後端資料
    fun responseObserve() : SharedFlow<RES>
    //socket client目前的狀態
    fun stateChangeObserve() : SharedFlow<State>

    fun destroy()
}