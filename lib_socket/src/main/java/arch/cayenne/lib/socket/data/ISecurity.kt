package arch.cayenne.lib.socket.data


interface ISecurity<REQ, BYTE, RES> {
    fun encrypt(data: REQ): BYTE?
    fun decrypt(data: BYTE): RES

    fun resetSecurity()
}