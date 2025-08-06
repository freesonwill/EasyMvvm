package arch.cayenne.module.bet

import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
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
import kotlinx.coroutines.withContext

class BettingRemoteManager(
    private val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) {

    private val _matchMarketNotifyFlow: MutableSharedFlow<List<BetNotifySelectionBean>> =
        MutableSharedFlow(replay = 1, extraBufferCapacity = 1)
    val matchMarketNotifyFlow: Flow<List<BetNotifySelectionBean>> = _matchMarketNotifyFlow

    private var socketConnectState: ConnectState? = null
    val isConnected: Boolean
        get() = socketConnectState == ConnectState.ConnectSuccess

    init {
        scope.launch {
            launch {
                socketManager.observeProtoMessage<Client.MatchMarketNotify>(ApiCode.MATCH_MARKET_NOTIFY)
                    .collect { res ->
                        if (res.error == null && res.data != null) {
                            val data = res.data!!
                            setMatchMarketNotifyData(data)
                        }
                    }
            }
            launch {
                socketManager.getConnectStateFlow().collect {
                    socketConnectState = it
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
                            this.serialValue = it.serialValue
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
                    serialValue = it.serialValue,
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
                    minAmount = data.min.toString().toMoney(),
                    maxAmount = data.max.toString().toMoney()
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
                    serialValue = it.serialValue,
                    minAmount = it.min.toString().toMoney(),
                    maxAmount = it.max.toString().toMoney()
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

    // 500-1003: 获取比赛详情
    suspend fun getMatchReq(matchId: Long): MatchWithMarkets? = withContext(scope.coroutineContext) {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.GetMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MATCH
        ) {
            Client.GetMatchReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }
        return@withContext if (result.error == null && result.data != null) {
            toMatchWithMarket(result.data!!.match)
        } else {
            null
        }
    }

    private fun toMatchWithMarket(originMatch: Common.Match): MatchWithMarkets {
        val markets = arrayListOf<MarketWithSelections>()
        originMatch.marketList.forEach { originMarket ->
            val selections = arrayListOf<SelectionBeanLite>()
            originMarket.marketDetailList.forEach { originMarketDetail ->
                originMarketDetail.selectionList.forEach { originSelection ->
                    selections.add(
                        SelectionBeanLite(
                            selectionId = originSelection.selectionId,
                            detailActive = originMarketDetail.active,
                            matchId = originMatch.matchId,
                            name = originSelection.name,
                            shortName = originSelection.shortName,
                            odds = originSelection.odds.toOdds(),
                            active = originSelection.active,
                            parlay = originSelection.parlay,
                        )
                    )
                }
            }
            markets.add(
                MarketWithSelections(
                    market = MarketBeanLite(
                        marketId = originMarket.marketId,
                        marketName = originMarket.marketName,
                        status = originMarket.status,
                        defaultSelectionCount = originMarket.marketDetailList.flatMap { it.selectionList }
                            .count()
                    ),
                    selections = selections
                )
            )
        }
        return MatchWithMarkets(
            match = MatchBean(
                matchId = originMatch.matchId,
                collect = originMatch.collect,
                basicInfo = MatchBasicInfoBean(
                    matchId = originMatch.basicInfo.matchId,
                    matchName = originMatch.basicInfo.matchName,
                    homeTeam = originMatch.basicInfo.homeTeam,
                    homeTeamId = originMatch.basicInfo.homeTeamId,
                    homeTeamIcon = originMatch.basicInfo.homeTeamIcon,
                    awayTeam = originMatch.basicInfo.awayTeam,
                    awayTeamId = originMatch.basicInfo.awayTeamId,
                    awayTeamIcon = originMatch.basicInfo.awayTeamIcon,
                    startTime = originMatch.basicInfo.startTime,
                    status = originMatch.basicInfo.status,
                    tournamentId = originMatch.basicInfo.tournamentId,
                    tournamentName = originMatch.basicInfo.tournamentName,
                    tournamentShortName = originMatch.basicInfo.tournamentShortName,
                    tournamentIcon = originMatch.basicInfo.tournamentIcon,
                    sportId = originMatch.basicInfo.sportId,
                    sportName = originMatch.basicInfo.sportName,
                    betStop = originMatch.basicInfo.betStop,
                    tournamentHot = originMatch.basicInfo.tournamentHot,
                    tournamentWeight = originMatch.basicInfo.tournamentWeight,
                    provider = originMatch.basicInfo.provider
                ),
                liveInfo = MatchLiveInfoBean(
                    clock = originMatch.basicInfo.liveInfo.clock,
                    rollClock = originMatch.basicInfo.liveInfo.rollClock,
                    period = originMatch.basicInfo.liveInfo.periodName,
                    score = originMatch.basicInfo.liveInfo.score,
                    liveVideo = originMatch.basicInfo.liveInfo.liveVideo,
                    charRoom = originMatch.basicInfo.liveInfo.chatRoom,
                    viewerCount = originMatch.basicInfo.liveInfo.viewerCount,
                    clockModified = originMatch.basicInfo.liveInfo.clockModified,
                    homeScore = originMatch.basicInfo.liveInfo.homeScore,
                    awayScore = originMatch.basicInfo.liveInfo.awayScore,
                    liveAnimation = originMatch.basicInfo.liveInfo.animationLiveUrl
                )
            ),
            markets = markets
        )
    }
}