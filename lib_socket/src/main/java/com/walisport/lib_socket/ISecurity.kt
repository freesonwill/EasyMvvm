package com.walisport.lib_socket


interface ISecurity<REQ, BYTE, RES> {
    fun encrypt(data: REQ): BYTE?
    fun decrypt(data: BYTE): RES
}