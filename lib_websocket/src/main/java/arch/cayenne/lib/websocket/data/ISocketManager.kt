package arch.cayenne.lib.websocket.data

import kotlinx.coroutines.flow.Flow

/**
 * @date: 2025/9/29 16:22
 * @description:
 */
interface ISocketManager {
    fun getSocketFlow(): Flow<IResponse>
    fun getConnectStateFlow(): Flow<ConnectState>
    val socketConnectState: SocketConnectState
}