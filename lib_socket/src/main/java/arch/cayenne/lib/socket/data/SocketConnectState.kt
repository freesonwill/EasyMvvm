package arch.cayenne.lib.socket.data

enum class SocketConnectState {
    None,
    Failure,
    Closed,
    Connecting,
    Reconnecting
}