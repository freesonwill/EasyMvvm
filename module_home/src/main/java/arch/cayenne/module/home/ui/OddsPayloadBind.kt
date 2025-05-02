package arch.cayenne.module.home.ui

interface OddsPayloadBind<T> {
    fun bindPayload(item: T, payload: List<Any>)
}