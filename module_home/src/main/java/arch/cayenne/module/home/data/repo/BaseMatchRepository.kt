package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchBeanLite
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.observeProtoMessage
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.bet.data.BetInsertBean
import arch.cayenne.module.home.data.model.MatchUpdateData
import arch.cayenne.module.home.data.model.toRoomData
import arch.cayenne.module.home.utils.setSelected
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

abstract class BaseMatchRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val betDao: BetDao,
    private val matchDao: MatchDao,
    private val infoDao: InfoDao,
    private val userDataManager: UserDataManager,
) : BaseRepository() {
    companion object {
        const val ONE_DAY_TIME_STAMP = 86399000L
        const val THIRTY_DAY_TIME_STAMP = 86399000L * 29
        const val DEFAULT_MATCH_SIZE = 10
    }

    protected val oddsDisplayType: Int
        get() {
            return userDataManager.getValue(UserDataKey.KEY_ODDS, OddsDisplayEnum.EU.value)
        }

    /**
     * 訂閱賽事，並且訂閱成功後會先馬上回傳一次訂閱賽事的資料
     * */
    suspend fun subscribeMatch(ids: List<Long>): ApiResponseState {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.SubscribeHomeMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SUBSCRIBE_HOME_MATCH,
        ) {
            Client.SubscribeHomeMatchReq.newBuilder().apply {
                this.addAllMatchId(ids)
            }.build()
        }
        if (res.error == null && res.data != null) {
            "訂閱比賽成功  ${res.data!!.matchNotifyList.map { it.matchId }}".logi(this::class.java.name)
            val matchUpdateData = res.data!!.matchNotifyList.filter { it.basicUpdate.status != 0 }.toRoomData()

            deleteMissingMatch(ids - matchUpdateData.ids.toSet())

            return ApiResponseState.Succeeded(updateFullMath(matchUpdateData))
        } else { return ApiResponseState.Failed(res.error) }
    }

    suspend fun cancelSubscribeMatch(ids: List<Long>): Boolean {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.CancelSubscribeHomeMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.CANCEL_SUBSCRIBE_HOME_MATCH,
        ) {
            Client.SubscribeHomeMatchReq.newBuilder().apply {
                this.addAllMatchId(ids)
            }.build()
        }
        return res.error == null && res.data != null
    }

    /**
     * 刪除賽事，通常是因為訂閱後沒有收到該賽事資料，表示該賽事已經結束
     * */
    protected open fun deleteMissingMatch(matchIds: List<Long>) {
        if (matchIds.isEmpty()) return
        matchDao.deleteMissingMatch(matchIds)
    }

    /**
     * 收藏賽事或是取消收藏賽事
     * **/
    @Transaction
    suspend fun matchCollect(item: MatchWithMarkets, collect: Boolean): ApiResponseState {
        val res = if (collect) {
            socketManager.sendAndWaitProtoMessageResponse<Client.AddCollectResp>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.ADD_COLLECT,
            ) {
                Client.AddCollectReq.newBuilder().apply {
                    this.addMatchId(item.match.matchId)
                }.build()
            }
        } else {
            socketManager.sendAndWaitProtoMessageResponse<Client.RemoveCollectReq>(
                scope = scope,
                dispatcher = Dispatchers.IO,
                apiCode = ApiCode.REMOVE_COLLECT,
            ) {
                Client.RemoveCollectReq.newBuilder().apply {
                    this.addMatchId(item.match.matchId)
                }.build()
            }
        }
        if (res.error == null && res.data != null) {
            matchDao.updateOnlyMatchCollect(item.match.matchId, collect)
            return ApiResponseState.Succeeded(matchDao.getOneMatchById(item.match.matchId, oddsDisplayType).setSelected(betDao))
        } else {
            return ApiResponseState.Failed(res.error)
        }
    }

    /**
     * 取得特定的match，藉由matchId
     * */
    suspend fun getOneMatchById(matchId: Long): MatchWithMarkets? {
        return matchDao.getOneMatchByIds(arrayListOf(matchId), oddsDisplayType).setSelected(betDao).firstOrNull()
    }

    /**
     * 已經跟後端訂閱後的賽事，收到的賽事資料回傳
     * */
    suspend fun observeMatchNotify(): Flow<MatchWithMarkets> {
        return socketManager.observeProtoMessage<Client.MatchNotify>(ApiCode.MATCH_NOTIFY).transform {
            if (it.error == null && it.data != null) {
                "收到比賽推播  ${it.data!!}".logi(this@BaseMatchRepository::class.java.simpleName)
                if (it.data!!.hasBasicUpdate() && it.data!!.basicUpdate.status == 0) {
                    deleteMissingMatch(arrayListOf(it.data!!.matchId))
                } else {
                    val matchUpdateData = arrayListOf(it.data!!).toRoomData()
                    val list = updateFullMath(matchUpdateData)
                    list.forEach { matchWithMarket -> emit(matchWithMarket) }
                }
            }
        }
    }

    /**
     * 更新首頁賽事資料，開始訂閱比賽與訂閱後收到比賽更新訊息時使用
     * @return 回傳更新後的賽事資料
     * */
    private suspend fun updateFullMath(updateData: MatchUpdateData): List<MatchWithMarkets> {
        return matchDao.updateFullMatch(
            updateData.ids,
            updateData.matchLites,
            updateData.markets,
            updateData.selections,
            updateData.matchMarketCrossRefs,
            updateData.marketSelectCrossRefs,
            oddsDisplayType,
        ).setSelected(betDao)
    }

    suspend fun updateLiveMatch(matchIds: List<Long>) : List<MatchWithMarkets> {
        val matchLites = arrayListOf<MatchBeanLite>()
        matchDao.getMatchByIds(matchIds).forEach { match ->
            val liveInfo = MatchLiveInfoBean(
                clock = match.liveInfo.clock + 1,
                rollClock = match.liveInfo.rollClock,
                period = match.liveInfo.period,
                score = match.liveInfo.score,
                liveVideo =match.liveInfo.liveVideo,
                charRoom = match.liveInfo.charRoom,
                viewerCount = match.liveInfo.viewerCount,
                clockModified = match.liveInfo.clockModified + 1000,
                homeScore = match.liveInfo.homeScore,
                awayScore = match.liveInfo.awayScore,
                liveAnimation = match.liveInfo.liveAnimation
            )
            matchLites.add(
                MatchBeanLite(
                    matchId = match.matchId,
                    status = match.basicInfo.status,
                    betStop = match.basicInfo.betStop,
                    startTime = match.basicInfo.startTime,
                    liveInfo = liveInfo,
                )
            )
        }
        return matchDao.updateOnlyMatch(matchLites.map { it.matchId }, matchLites, oddsDisplayType).setSelected(betDao)
    }

    /**
     * 單純根據match id取得比賽
     * @return 根據條件query的賽事資料
     * */
    suspend fun queryFullMatches(matchIds: List<Long>, selectedIds: List<Long>? = null) : List<MatchWithMarkets> {
        val result = matchDao.getOneMatchByIds(matchIds, oddsDisplayType).setSelected(betDao, selectedIds)
        return matchIds.mapNotNull { id -> result.find { it.match.matchId == id } }
    }

    fun matchSelectionInsertBean(
        match: MatchBean,
        market: MarketBeanLite,
        selectionBean: SelectionBeanLite
    ): BetInsertBean = BetInsertBean(
        sportId = match.basicInfo.sportId,
        matchId = selectionBean.matchId,
        marketId = selectionBean.marketId,
        matchStatus = match.basicInfo.status,
        marketName = market.marketName,
        score = match.liveInfo.score,
        selectionId = selectionBean.selectionId,
        name = selectionBean.name,
        odds = selectionBean.odds,
        leagueName = match.basicInfo.tournamentName,
        matchName = match.basicInfo.matchName,
        isActive = selectionBean.active,
        isPlaying = match.basicInfo.status == 5,
        isParlay = selectionBean.parlay,
        provider = match.basicInfo.provider
    )

    /**
     * 监听用户令牌的变化。
     *
     * 此方法通过观察用户数据管理器中存储的用户令牌（KEY_TOKEN），
     * 并将其转换为一个布尔值流，表示令牌是否存在且非空。
     *
     * @return 一个 `Flow<Boolean>`，当令牌存在且非空时发射 `true`，否则发射 `false`。
     */
    suspend fun observeUserToken(): Flow<Boolean> {
        return userDataManager.observe<String>(UserDataKey.KEY_TOKEN).transform {
            emit(it.isNotEmpty())
        }
    }
}