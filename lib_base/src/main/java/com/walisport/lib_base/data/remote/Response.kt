package com.walisport.lib_base.data.remote

sealed class Response {
    data object Success : Response()
    data class Failed(val code: Int, val desc: String) : Response()
}