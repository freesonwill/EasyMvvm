package com.walisport.module.live.data

import android.annotation.SuppressLint
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.SelectionsEdit
import arch.cayenne.lib.skin.SkinnableManager
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Client.MatchBasicUpdate
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Market
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveMainRepository(
    private val remoteManager: LiveRemoteManager,
    private val database: GameDatabase,
    private val userManager: UserDataManager,
    private val skinManager: SkinnableManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeInfo(): Flow<InfoBean?> = database.infoDao().observeInfo().flowOn(Dispatchers.IO)
    fun observeMatchBean(matchId: Long) = database.liveMatchDao().observeMatchById(matchId).flowOn(Dispatchers.IO)
    fun observeLoginChange() = database.infoDao().observeIsLogin()
    // 500-1003: 获取比赛详情
    @SuppressLint("SuspiciousIndentation")
    suspend fun getMatchRes(matchId: Long):ApiResponseState = withContext(scope.coroutineContext)  {
        clearMatchCache()
        val state :ApiResponseState= remoteManager.getMatchReq(scope, matchId)
        if (state is ApiResponseState.Succeeded<*>) {
         val data : Common.Match? =  state.data as Common.Match
          val matchFullData = data?.toRoomData()
            if (matchFullData != null) {
                database.liveMatchDao().insertFullMatch(
                    matches = matchFullData.match,
                    markets = matchFullData.markets,
                    selections = matchFullData.selections,
                    selectionsRecord = matchFullData.selectionsRecord,
                )
                return@withContext( ApiResponseState.Succeeded(matchFullData.match.find { it.matchId == matchId }))
            }
        }
        return@withContext(state)
    }

    private fun clearMatchCache() {
        database.liveMatchDao().clearAllMatch()
    }

    suspend fun registerMatchInfoNotify(matchId: Long) {
        remoteManager.registerMatchInfoNotify(scope, matchId)
    }

    suspend fun observeMatchInfoNotify() {
        remoteManager.observeMatchInfoNotify().collect {
            scope.launch(Dispatchers.IO) {
                updateFullMatchInfo(
                    if (it.hasBasicUpdate()) {
                        it.basicUpdate
                    } else {
                        null
                    }, it.marketUpdateList, it.matchId
                )
            }
        }
    }

    suspend fun updateFullMatchInfo(
        marketInfo: MatchBasicUpdate?, marketUpdate: List<Market>, matchId: Long
    ) {
        marketInfo?.let {
            if (marketInfo.hasLiveInfo()) {
                database.liveMatchDao().updateNotifyMatchInfo(
                    matchId = matchId,
                    status = marketInfo.status,
                    betStop = marketInfo.betStop,
                    startTime = marketInfo.startTime,
                    clock = marketInfo.liveInfo.clock,
                    rollClock = marketInfo.liveInfo.rollClock,
                    period = marketInfo.liveInfo.period,
                    score = marketInfo.liveInfo.score,
                    liveVideo = marketInfo.liveInfo.liveVideo,
                    charRoom = marketInfo.liveInfo.chatRoom,
                    viewerCount = marketInfo.liveInfo.viewerCount,
                    clockModified = marketInfo.liveInfo.clockModified,
                    animationLiveUrl = marketInfo.liveInfo.animationLiveUrl
                )
            } else {
                database.liveMatchDao().updateNotifyMatchInfo(
                    matchId = matchId,
                    status = marketInfo.status,
                    betStop = marketInfo.betStop,
                    startTime = marketInfo.startTime,
                )
            }
        }
        val selections =
            marketUpdate.selectionsToRoomData(database.liveMatchDao().getSelectionsRecord())
        database.liveMatchDao().deleteSelectionsEdit()
        database.liveMatchDao().updateLiveSelectionBean(
            selections.selectionsEdit,
            selections.selectionsRecord,
            selections.selectionsAdd,
            selections.selectionsDelete,
            selections.marketsAdd,
            selections.selectionsEditId
        )
    }

    fun unregisterMatchInfoNotify(matchId: Long) {
        remoteManager.unregisterMatchInfoNotify(scope, matchId)
    }

   suspend fun getSelectionsEdit() :List<SelectionsEdit>{
       return withContext(IO){
           database.liveMatchDao().getSelectionsEdit()
       }
    }

    fun clearAllMatch() {
        scope.launch(Dispatchers.IO) {
            database.liveMatchDao().clearAllMatch()
        }
    }

    suspend fun registerMatchStaticsNotify(matchId: Long): Sloth.MatchLiveData? {
        return remoteManager.registerMatchStaticsNotify(scope, matchId)
    }

    suspend fun unregisterStatisticsNotify() {
        remoteManager.unregisterMatchStaticsNotify(scope)
    }

    suspend fun observeMatchStaticsNotify(): Flow<Sloth.MatchLiveData> {
        return remoteManager.observeMatchStaticsNotify()
    }

    fun reconnect() {
        remoteManager.connectToServer()
    }

    fun getSkinType():String {
        return userManager.getValue(UserDataKey.KEY_SKIN, SkinType.DEFAULT)
    }

    suspend fun setSkinType(type:String){
        userManager.setKeyValue(UserDataKey.KEY_SKIN,type)
        skinManager.loadSkin(type)
    }
}

