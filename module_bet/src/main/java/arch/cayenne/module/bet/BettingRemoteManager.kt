package arch.cayenne.module.bet

import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.remote.ComboBetDataModel
import arch.cayenne.module.bet.data.remote.ComboMultiBetInfo
import arch.cayenne.module.bet.data.remote.ReserveBetDataModel
import arch.cayenne.module.bet.data.remote.SingleBetDataModel
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BettingRemoteManager(private val socketManager: WebSocketManager) {

    suspend fun singleBet(scope: CoroutineScope, bean: BetBean, money: Long): SingleBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.SingleBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SINGLE_BET,
        ) {
            Client.SingleBetReq.newBuilder().apply {
                this.matchId = bean.matchId
                this.selectionId = bean.selectionLiteBean.id
                this.odds = bean.selectionLiteBean.odds
                this.betAmount = money.getMoney()
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

    suspend fun reserveBet(scope: CoroutineScope, bean: BetBean, money: Long): ReserveBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ReserveBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SINGLE_BET,
        ) {
            Client.ReserveBetReq.newBuilder().apply {
                this.setBet(Common.BetOption.newBuilder().apply {
                    this.matchId = bean.matchId
                    this.selectionId = bean.selectionLiteBean.id
                    this.odds = bean.selectionLiteBean.odds
                })
                this.betAmount = money.getMoney()
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

    suspend fun comboBet(
        scope: CoroutineScope,
        beans: List<BetBean>,
        multi: List<ComboMultiBetBean>
    ): ComboBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.MultipleBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.COMBO_BET,
        ) {
            Client.MultipleBetReq.newBuilder().apply {
                this.addAllBet(
                    beans.map {
                        Common.BetOption.newBuilder().apply {
                            this.matchId = it.matchId
                            this.selectionId = it.selectionLiteBean.id
                            this.odds = it.selectionLiteBean.odds
                        }.build()
                    }
                )
                this.addAllCombo(
                    multi.map {
                        Common.BetCombo.newBuilder().apply {
                            this.serialValue = if (it.combo == 1) 0 else it.combo
                            this.betAmount = it.inputMoney.getMoney()
                            this.oddsChange = 2
                        }.build()
                    }
                )
            }.build()
        }
        return if (res.error == null && res.data != null) {
            val data = res.data!!
            val placeBetInfo = data.placeBetInfoList.map {
                ComboMultiBetInfo(
                    orderId = it.orderId,
                    comboValue = it.serialValue,
                    orderStatus = it.orderStatus,
                    orderStatusMsg = it.orderStatusMsg
                )
            }
            ComboBetDataModel(
                data.success,
                data.message,
                placeBetInfo
            )
        } else {
            null
        }
    }
}