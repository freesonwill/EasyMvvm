package arch.cayenne.lib.websocket.data

enum class SocketConnectState {
    None,
    Failure,
    Closed,
    Connecting,
    Reconnecting
}