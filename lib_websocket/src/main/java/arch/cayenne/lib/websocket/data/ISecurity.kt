package arch.cayenne.lib.websocket.data

import arch.cayenne.lib.websocket.NativeLib


interface ISecurity<REQ, BYTE, RES> {
    fun encrypt(data: REQ): BYTE?
    fun decrypt(data: BYTE): RES

    fun resetSecurity(type:Int = NativeLib.CIPHER_TYPE_PB)
}