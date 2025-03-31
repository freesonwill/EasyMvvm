package com.walisport.lib_socket.data

enum class SocketConnectState {
    None,
    Failure,
    Closed,
    Connecting,
    Reconnecting
}