package arch.cayenne.module.home.repository

import androidx.room.Transaction
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.database.entity.TournamentMatchRef
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.observeProtoMessage
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.MatchUpdateData
import arch.cayenne.module.home.data.toRoomData
import arch.cayenne.module.home.viewmodel.BaseGameListViewModel.Companion.DEFAULT_MATCH_SIZE
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class HomeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val database: GameDatabase
) : BaseRepository() {
    private val sportDao = database.sportDao()
    private val tournamentDao = database.tournamentDao()
    private val matchDao = database.matchDao()
    private val betDao = database.betDao()

    companion object {
        const val ONE_DAY_TIME_STAMP = 86399000L
    }

    @Transaction
    suspend fun getSportStatistical(): List<SportDataModel>? {
        //clear sport table
        clearSportCache()
        //先從DB拿取
//        val queryResult = sportDao.querySportsMatchCount(playType)
//        if (queryResult.isNotEmpty()) {
//            return queryResult
//        }
        //從API拿取
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.StatisticalResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.STATISTICAL,
        ) {
            Client.StatisticalReq.newBuilder().build()
        }
        return if (res.error == null && res.data != null) {
            return saveSports(res.data!!)
        } else {
            res.error
            null
        }

    }
    private fun saveSports(data: Client.StatisticalResp): List<SportDataModel> {
        val sportMap = hashMapOf<Int, SportBean>()
//        val categoryList = arrayListOf<PlayTypeSportCrossRef>()
        data.statisticalList.forEach { play ->
            play.sportStatisticalList.forEachIndexed { index, sport ->
                val bean = SportBean(
                    sportId = sport.sportId,
                    sportName = sport.sportName,
                    matchCount = sport.matchCount,
                    sportOrder = index
                )
                sportMap[bean.sportId] = bean
//                val category = PlayTypeSportCrossRef(
//                    sportId = sport.sportId,
//                    playType = play.playType,
//                    matchCount = sport.matchCount,
//                    sportOrder = index
//                )
//                categoryList.add(category)
            }
        }
        sportDao.insert(sportMap.map{ it.value }.toList())
//        sportDao.insertSportCrossRef(categoryList)
        return sportDao.querySportsMatchCount()
    }

    private fun clearSportCache() {
        sportDao.clearSports()
    }
    @Transaction
    suspend fun getTenTournaments(playType: Int, sportId: Int): List<TournamentDataModel>? {
        clearTournamentCache()
        clearMatchCache()
        //TODO 如果更多頁點擊了不在這十個之中的tab則會新增於tab list(ui層, 不存db)
        //先從DB拿取
//        val queryResult = tournamentDao.queryTournamentWithLimit(playType, sportId, 10)
//        if (queryResult.isNotEmpty()) {
//            return queryResult
//        }
        //從API拿取
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListTournamentResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.TOURNAMENT,
        ) {
            Client.ListTournamentReq.newBuilder().apply {
                this.sportId = sportId
                this.playType = playType
                this.size = 0
            }.build()
        }
        return if (res.error == null && res.data != null) {
            saveTournaments(playType, sportId, res.data!!)
        } else {
            null
        }
    }

    private fun saveTournaments(playType: Int, sportId: Int, data: Client.ListTournamentResp): List<TournamentDataModel> {
        val tournamentList = arrayListOf<TournamentBean>()
//        val sportTournamentCrossRefList = arrayListOf<SportTournamentCrossRef>()
        data.tournamentList.forEach { tournament ->
            tournamentList.add(
                TournamentBean(
                    id = tournament.id,
                    playType = playType,
                    sportId = sportId,
                    name = tournament.name,
                    simpleName = tournament.simpleName,
                    icon = tournament.icon,
                    hot = tournament.hot,
                    weight = tournament.weight,
                )
            )
//            sportTournamentCrossRefList.add(
//                SportTournamentCrossRef(
//                    tournamentId = tournament.id,
//                    sportId = sportId,
//                    playType = playType,
//                    hot = tournament.hot,
//                    weight = tournament.weight
//                )
//            )
        }
        tournamentDao.insert(tournamentList)
//        tournamentDao.insertTournamentRef(sportTournamentCrossRefList)
        return tournamentDao.queryTournamentWithLimit(10)
    }

    private fun clearTournamentCache() {
        tournamentDao.clearTournaments()
    }
    private fun clearMatchCache() {
        matchDao.clearAllMatch()
    }

    /**
     * 根據不同的條件，從api或是db(優先)取得賽事資料，如果從api來的話，拿到後會先存進資料庫內
     * */
    suspend fun getAllMatch(playType: Int, sportId: Int, tournamentId: Int, page: Int, startTime: Long) : List<MatchWithMarkets> {
        //先從DB拿取
        val queryResult = queryFullMatch(playType, tournamentId, page, startTime)
        if (queryResult.isNotEmpty()){
            return queryResult
        }
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_MATCH,
        ) {
            Client.ListMatchReq.newBuilder().apply {
                this.sportId = sportId
                this.playType = playType
                this.tournamentId = tournamentId
                this.page = page
                this.size = DEFAULT_MATCH_SIZE
                if (startTime != 0L){
                    this.startTime = startTime
                    this.endTime = startTime + ONE_DAY_TIME_STAMP
                }
            }.build()
        }

        if (resp.error == null && resp.data != null) {
            val matchFullData = resp.data!!.matchList.toRoomData()
            val tournamentMatchRefs = resp.data!!.matchList.mapIndexed { index, match ->
                TournamentMatchRef(
                    playType = playType,
                    tournamentId = tournamentId,
                    page = page,
                    startTime = startTime,
                    matchId = match.matchId,
                    order = page * 100 + index
                )
            }
            database.matchDao().insertFullMatch(
                tournamentMatchRefs = tournamentMatchRefs,
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )
            return queryFullMatch(playType, tournamentId, page, startTime)
        }
        return arrayListOf()
    }

    /**
     * 訂閱賽事，並且訂閱成功後會先馬上回傳一次訂閱賽事的資料
     * */
    suspend fun subscribeMatch(ids: List<Long>): List<MatchWithMarkets> {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.SubscribeMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SUBSCRIBE_MATCH,
        ) {
            Client.SubscribeMatchReq.newBuilder().apply {
                this.addAllMatchId(ids)
            }.build()
        }
        if (res.error == null && res.data != null && res.data!!.success) {
            "訂閱比賽成功  ${res.data!!.matchNotifyList.map { it.matchId }}".logi(this::class.java.name)
            val matchUpdateData = res.data!!.matchNotifyList.toRoomData()
           return updateFullMath(matchUpdateData)
        } else { return arrayListOf() }
    }

    suspend fun cancelSubscribeMatch(ids: List<Long>): Boolean {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.CancelSubscribeMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.CANCEL_SUBSCRIBE_MATCH,
        ) {
            Client.SubscribeMatchReq.newBuilder().apply {
                this.addAllMatchId(ids)
            }.build()
        }
        if (res.error == null && res.data != null && res.data!!.success) {
            "取消訂閱比賽成功?  ${res.data!!.success}".logi(this::class.java.name)
            return res.data!!.success
        } else { return false }
    }

    @Transaction
    suspend fun matchCollect(item: MatchWithMarkets, collect: Boolean): MatchWithMarkets? {
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
            return matchDao.getOneMatchById(item.match.matchId)
        }
        return null
    }

    suspend fun observeBalance(): Flow<Long> = database.infoDao().observeBalance()

    /**
    * 取得特定的match，藉由matchId
    * */
    suspend fun getOneMatchById(matchId: Long): MatchWithMarkets? {
        return matchDao.getOneMatchByIds(arrayListOf(matchId)).setSelected().firstOrNull()
    }

    /**
     * 已經跟後端訂閱後的賽事，收到的賽事資料回傳
     * */
    suspend fun observeMatchNotify(): Flow<MatchWithMarkets> {
        return socketManager.observeProtoMessage<Client.MatchNotify>(ApiCode.MATCH_NOTIFY).transform {
            if (it.error == null && it.data != null) {
                "收到比賽推播  ${it.data!!.matchId}".logi(this::class.java.name)
                val matchUpdateData = arrayListOf(it.data!!).toRoomData()
                val list = updateFullMath(matchUpdateData)
                list.forEach { matchWithMarket -> emit(matchWithMarket) }
            }
        }
    }

    /**
    * 更新首頁賽事資料，開始訂閱比賽與訂閱後收到比賽更新訊息時使用
     * @return 回傳更新後的賽事資料
    * */
    private suspend fun updateFullMath(updateData: MatchUpdateData): List<MatchWithMarkets> {
        return matchDao.updateFullMatch(
            updateData.matchLites,
            updateData.markets,
            updateData.selections,
            updateData.matchMarketCrossRefs,
            updateData.marketSelectCrossRefs
        ).setSelected()
    }

    /**
     * 單純根據頁數和時間取得資料，用來第一次取得比賽和下一頁取得和時間區間取得比賽
     * @return 根據條件query的賽事資料
     * */
    private suspend fun queryFullMatch(playType: Int, tournamentId: Int, page: Int, startTime: Long) : List<MatchWithMarkets> {
        return database.matchDao().getFullMatch(playType, tournamentId, page, startTime).setSelected()
    }

    /**
     * 找出投注單中未投注的selection，把它設為點擊狀態
     * */
    private suspend fun List<MatchWithMarkets>.setSelected(): List<MatchWithMarkets> {
        val betSelections = betDao.getCurrentSelectionIds().toSet()  //在投注單內的內容
        this.forEach { match ->
            match.markets.forEach { market ->
                market.selections.forEach {
                    it.isSelected = betSelections.contains(it.selectionId)
                }
            }
        }
        return this
    }

    fun clearCurrentMatch(playType: Int, tournamentId: Int, startTime: Long) {
        matchDao.deleteCurrentTournamentMatchRef(playType, tournamentId, startTime)
    }
}