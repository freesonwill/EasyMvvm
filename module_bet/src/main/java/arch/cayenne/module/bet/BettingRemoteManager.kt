package arch.cayenne.module.bet

import arch.cayenne.lib.common.utils.ext.IntExt.getOdds
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.bet.data.remote.ReserveBetDataModel
import arch.cayenne.module.bet.data.remote.SingleBetDataModel
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BettingRemoteManager(private val socketManager: WebSocketManager) {

    suspend fun singleBet(scope: CoroutineScope, bean: BetBean, money: Int): SingleBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.SingleBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SINGLE_BET,
        ) {
            Client.SingleBetReq.newBuilder().apply {
                this.matchId = bean.matchId.toLong()
                this.selectionId = bean.selection.id.toLong()
                this.odds = bean.selection.odds.getOdds()
                this.betAmount = money.getOdds()
                this.oddsChange = 2
            }.build()
        }
        return if (res.error == null && res.data != null) {
            val data = res.data!!
            SingleBetDataModel(
                data.success,
                data.message,
                data.orderId,
                data.orderStatus,
                data.orderStatusMsg,
            )
        } else {
            null
        }
    }

    suspend fun reserveBet(scope: CoroutineScope, bean: BetBean, money: Int): ReserveBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ReserveBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SINGLE_BET,
        ) {
            Client.ReserveBetReq.newBuilder().apply {
                this.setBet(Common.BetOption.newBuilder().apply {
                    this.matchId = bean.matchId.toLong()
                    this.selectionId = bean.selection.id.toLong()
                    this.odds = bean.selection.odds.getOdds()
                })
                this.betAmount = money.getOdds()
            }.build()
        }
        return if (res.error == null && res.data != null) {
            val data = res.data!!
            ReserveBetDataModel(
                data.success,
                data.message
            )
        } else {
            null
        }
    }
}