package com.walisport.lib_common.websocket.model

enum class SocketConnectState {
    None,
    Failure,
    Closed,
    Connecting,
    Reconnecting
}