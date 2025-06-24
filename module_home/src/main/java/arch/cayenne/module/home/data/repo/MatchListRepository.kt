package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.TournamentMatchRef
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.model.toRoomData
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class MatchListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val betDao: BetDao,
    private val matchDao: MatchDao,
) : BaseMatchRepository(scope, socketManager, betDao, matchDao) {
    /**
     * 根據不同的條件，從api或是db(優先)取得賽事資料，如果從api來的話，拿到後會先存進資料庫內
     * @param playType : 一級導航欄
     * @param sportId : 二級導航欄
     * @param tournamentId : 聯賽id
     * @param page : 頁數
     * @param date : 0 -> All、其餘時間為該日期的start time
     * @param startTime : 時間區間起始，如果非早盤類型為0，早盤的"ALL"為隔日早上00:00:00
     * @param endTime : 時間區間結束，如果非早盤類型為0，早盤的"ALL"為隔日開始起算30日
     * */
    suspend fun getAllMatch(
        playType: Int,
        sportId: Int,
        tournamentId: Int,
        page: Int,
        date: Long,
        startTime: Long,
        endTime: Long
    ) : Boolean {
//        val req = Client.ListMatchReq.newBuilder().apply {
//            this.sportId = sportId
//            this.playType = playType
//            this.tournamentId = tournamentId
//            this.page = page
//            this.size = DEFAULT_MATCH_SIZE
//            if (startTime != 0L){
//                this.startTime = startTime
//                this.endTime = startTime + ONE_DAY_TIME_STAMP
//            }
//        }.build()
//        socketManager.send(req.asRemoteRequest(ApiCode.LIST_MATCH))
        //-----------------------------------------
        val last = matchDao.queryLastMatch(playType, tournamentId, date)
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_MATCH,
        ) {
            Client.ListMatchReq.newBuilder().apply {
                this.sportId = sportId
                this.playType = playType
                this.tournamentId = tournamentId
                this.size = DEFAULT_MATCH_SIZE
                this.startTime = startTime
                this.endTime = endTime
                if (last != null) {
                    this.cursorMatchId = last.matchId
                    this.cursorMatchStartTime = last.basicInfo.startTime
                }
            }.build()
        }

        if (resp.error == null && resp.data != null) {
            if (resp.data!!.matchList.isNullOrEmpty()) {
                return false
            }
            val matchFullData = resp.data!!.matchList.toRoomData()
            "新增比賽 tournamentId = $tournamentId matchId = ${matchFullData.match.map { it.matchId }} 進入資料庫".logi(
                HomeRepository::class.java.simpleName)
            val tournamentMatchRefs = resp.data!!.matchList.mapIndexed { index, match ->
                TournamentMatchRef(
                    playType = playType,
                    tournamentId = tournamentId,
                    page = page,
                    date = date,
                    matchId = match.matchId,
                    order = page * 100 + index
                )
            }
            matchDao.insertTournamentMatchRef(tournamentMatchRefs)
            matchDao.insertMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )
            return true
        }
        return false
    }

    fun clearCurrentMatch(playType: Int, tournamentId: Int, date: Long) {
        matchDao.deleteCurrentTournamentMatchRef(playType, tournamentId, date)
    }

    fun observeMatchChange(playType: Int, tournamentId: Int) : Flow<List<TournamentMatchRef>> {
        //觀察後端的500-1002（获取比赛列表）回傳
        return matchDao.observeMatchChange(playType, tournamentId)
    }
}