package arch.cayenne.lib.socket.data

enum class ApiCode(val mid: Short, val sid: Short) {
    LOGIN(7,7),
    PING(7,100)
}