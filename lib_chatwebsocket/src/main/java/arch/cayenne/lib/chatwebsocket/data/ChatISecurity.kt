package arch.cayenne.lib.chatwebsocket.data


interface ChatISecurity<REQ, BYTE, RES> {
    fun encrypt(data: REQ): BYTE?
    fun decrypt(data: BYTE): RES
    fun destroySecurity()
    fun resetSecurity()
}