package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.data.SocketResponseData
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveBetRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) : BaseRepository() {


    suspend fun getOrderReq(
        status: Int,
        page: Int,
        pageSize: Int,
        sportId: Int,
        matchId: Long,
        startTime: Long? = null,
        endTime: Long? = null,
    ): List<Common.Order>? {

        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetOrderResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_ORDER
        ) {
            Client.GetOrderReq.newBuilder().apply {
                this.status = status
                this.page = page
                this.pageSize = pageSize
                this.addSportId(sportId)
                this.matchId = matchId
                startTime?.let { this.startTime = startTime }
                endTime?.let { this.endTime = endTime }
            }.build()
        }
        if(result.error != null && result.data != null){
            return result.data!!.orderList
        }
        return null
    }


    suspend fun getReserveOrder(
        sportId: Int,
        matchId: Long,
        startTime: Long? = null,
        endTime: Long? = null
    ): List<Common.ReserveOrder>? {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetReserveOrderResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GER_RESERVE_ORDER
        ) {
            Client.GetReserveOrderReq.newBuilder().apply { }.build()
        }

        if(result.error != null && result.data != null){
            return result.data!!.orderList
        }
        return null
    }


}