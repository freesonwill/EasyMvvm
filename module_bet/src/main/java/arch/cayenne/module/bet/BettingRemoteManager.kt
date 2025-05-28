package arch.cayenne.module.bet

import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.observeProtoMessage
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.bet.data.BetNotifySelectionBean
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.remote.ComboBetDataModel
import arch.cayenne.module.bet.data.remote.ComboMultiBetInfo
import arch.cayenne.module.bet.data.remote.ComboRiskDataModel
import arch.cayenne.module.bet.data.remote.ReserveBetDataModel
import arch.cayenne.module.bet.data.remote.SingleBetDataModel
import arch.cayenne.module.bet.data.remote.SingleRiskDataModel
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class BettingRemoteManager(
    private val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) {

    private val _matchMarketNotifyFlow: MutableSharedFlow<List<BetNotifySelectionBean>> =
        MutableSharedFlow(replay = 1, extraBufferCapacity = 1)
    val matchMarketNotifyFlow: Flow<List<BetNotifySelectionBean>> = _matchMarketNotifyFlow

    init {
        scope.launch {
            socketManager.observeProtoMessage<Client.MatchMarketNotify>(ApiCode.MATCH_MARKET_NOTIFY)
                .collect { res ->
                    if (res.error == null && res.data != null) {
                        val data = res.data!!
                        setMatchMarketNotifyData(data)
                    }
                }
        }
    }

    suspend fun singleBet(bean: BetSelectionBean, money: Long): SingleBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.SingleBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SINGLE_BET,
        ) {
            Client.SingleBetReq.newBuilder().apply {
                this.matchId = bean.matchId
                this.selectionId = bean.selectionId
                this.odds = bean.odds.getOdds()
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
            )
        } else {
            null
        }
    }

    suspend fun reserveBet(
        bean: BetSelectionBean,
        reserveOdds: Int,
        money: Long
    ): ReserveBetDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ReserveBetResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.RESERVE_BET,
        ) {
            Client.ReserveBetReq.newBuilder().apply {
                this.setBet(Common.BetOption.newBuilder().apply {
                    this.matchId = bean.matchId
                    this.selectionId = bean.selectionId
                    this.odds = reserveOdds.getOdds()
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
        beans: List<BetSelectionBean>,
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
                            this.selectionId = it.selectionId
                            this.odds = it.odds.getOdds()
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
                    comboValue = if (it.serialValue == 0) 1 else it.serialValue,
                    orderStatus = it.orderStatus,
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

    suspend fun getSingleRisk(
        matchId: Long,
        selectionId: Long
    ): SingleRiskDataModel? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.GetSingleRiskResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_SINGLE_RISK,
        ) {
            Client.GetSingleRiskReq.newBuilder().apply {
                val risk = Common.SelectionBase.newBuilder().apply {
                    this.matchId = matchId
                    this.selectionId = selectionId
                }.build()
                this.addSelection(risk)
            }.build()
        }
        return if (res.error == null && res.data != null) {
            res.data!!.riskList.firstOrNull()?.let { data ->
                SingleRiskDataModel(
                    matchId = data.matchId,
                    selectionId = data.selectionId,
                    minAmount = data.min,
                    maxAmount = data.max
                )
            }
        } else {
            null
        }
    }

    suspend fun getComboRisk(
        beans: List<BetSelectionBean>
    ): List<ComboRiskDataModel>? {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.GetComboRiskResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_COMBO_RISK,
        ) {
            Client.GetComboRiskReq.newBuilder().apply {
                val risk = beans.map { bean ->
                    Common.SelectionBase.newBuilder().apply {
                        this.matchId = bean.matchId
                        this.selectionId = bean.selectionId
                    }.build()
                }
                this.addAllSelection(risk)
            }.build()
        }
        return if (res.error == null && res.data != null) {
            val data = res.data!!
            data.riskList.map {
                ComboRiskDataModel(
                    combo = if (it.serialValue == 0) 1 else it.serialValue,
                    minAmount = it.min,
                    maxAmount = it.max
                )
            }
        } else {
            null
        }
    }

    suspend fun registerMatchMarketNotify(ids: List<Client.MarketIdBase>) {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.SubscribeMatchMarketResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SUBSCRIBE_MATCH_MARKET,
        ) {
            Client.SubscribeMatchMarketReq.newBuilder().apply {
                this.addAllMarket(
                    ids
                )
            }.build()
        }
        if (res.error == null && res.data != null) {
            res.data!!.matchNotifyList.forEach {
                setMatchMarketNotifyData(it)
            }
        }
    }

    fun unregisterMatchMarketNotify(ids: List<Client.MarketIdBase>) {
        scope.launch {
            socketManager.sendAndWaitProtoMessageResponse<Client.CancelSubscribeMatchMarketResp>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.CANCEL_SUBSCRIBE_MATCH_MARKET,
            ) {
                Client.SubscribeMatchMarketReq.newBuilder().apply {
                    this.addAllMarket(
                        ids
                    )
                }.build()
            }
        }
    }

    private fun setMatchMarketNotifyData(data: Client.MatchMarketNotify) {
        val matchId = data.matchId
        val selection = data.marketUpdateList
            .flatMap { it.marketDetailList }  // 展開所有 MarketDetail
            .flatMap { it.selectionList }
            .map { selection ->
                BetNotifySelectionBean(
                    matchId,
                    selection.selectionId,
                    selection.odds.toOdds(),
                    selection.active,
                    selection.parlay
                )
            }
        if (selection.isNotEmpty()) {
            _matchMarketNotifyFlow.tryEmit(selection)
        }
    }
}