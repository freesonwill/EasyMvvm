package com.walisport.module.setting.data

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Match
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LanguageRepository(
    override val scope: CoroutineScope,
    private val manager: UserDataManager,
    private val socketManager: WebSocketManager,
    private val languageManager: LanguageManager,
    private val betDao: BetDao
) : BaseRepository() {

    fun observeComboBetCount() = betDao.observeComboCount()

    //设置语言类型
    fun getLanguageType(): LanguageType {
        val lang = manager.getValue(UserDataKey.KEY_LANGUAGE, LanguageType.LANGUAGE_SIMPLE.value)
        return LanguageType.findLanguage(lang)
    }

    private fun setLanguageType(type: LanguageType) {
        scope.launch {
            manager.setKeyValue(UserDataKey.KEY_LANGUAGE, type.value)
            languageManager.changeLanguage(Locale(type.value))
        }
    }


    suspend fun saveLanguageType(type: LanguageType) = withContext(scope.coroutineContext) {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.UpdateSettingResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.UPDATE_SYSTEM_SETTING,
        ) {
            Client.UpdateSettingReq.newBuilder().apply {
                this.setting = getSystemSetting(type)
            }.build()
        }
        if (resp.error == null && resp.data != null) {
            setLanguageType(type)
            updateBet()
            ApiResponseState.Succeeded(resp.data)
        } else {
            ApiResponseState.Failed(error = resp.error)
        }
    }

    private fun getSystemSetting(type: LanguageType): Common.Setting {
        return Common.Setting.newBuilder().apply {
            lang = when (type) {
                LanguageType.LANGUAGE_ENGLISH -> "en-US"
                LanguageType.LANGUAGE_ID -> "id-ID"
                LanguageType.LANGUAGE_PT -> "pt-PT"
                else -> "zh-CN"
            }
        }.build()
    }

    private fun updateBet() {
        scope.launch {
            betDao.getCurrentBet()?.let { bet ->
                betDao.getSelections(bet.betId).let { selections ->
                    if (selections.isNotEmpty()) {
                        selections.forEach { selection ->
                            val resp =
                                socketManager.sendAndWaitProtoMessageResponse<Client.GetMatchResp>(
                                    scope = scope,
                                    dispatcher = Dispatchers.IO,
                                    apiCode = ApiCode.GET_MATCH,
                                ) {
                                    Client.GetMatchReq.newBuilder().apply {
                                        matchId = selection.matchId
                                    }.build()
                                }
                            if (resp.error == null && resp.data != null) {
                                val match = resp.data!!.match
                                val newMatch = toMatchWithMarket(match)
                                newMatch.markets.find { market ->
                                    market.selections.find { it.selectionId == selection.selectionId } != null
                                }?.let { market ->
                                    val marketName = market.market.marketName
                                    val name = market.selections.find { it.selectionId == selection.selectionId }?.name ?: selection.name
                                    val leagueName = match.basicInfo.tournamentName
                                    val matchName = match.basicInfo.matchName
                                    betDao.updateLanguage(bet.betId, selection.selectionId, marketName = marketName, name = name, leagueName = leagueName, matchName = matchName)
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private fun toMatchWithMarket(originMatch: Match): MatchWithMarkets {
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