package com.walisport.lib_socket.data

enum class ApiCode(val mid: Short, val sid: Short) {
    LOGIN(7,7),
    PING(0,2)
}