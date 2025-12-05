package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.EarlyTournamentMatchRef
import arch.cayenne.lib.database.entity.TournamentMatchRef
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.model.toRoomData
import arch.cayenne.module.home.ui.viewmodel.BaseMatchViewModel.Companion.INITIAL_PAGE
import arch.cayenne.module.home.ui.viewmodel.LoadMatchType
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class MatchListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val betDao: BetDao,
    private val matchDao: MatchDao,
    private val infoDao: InfoDao,
    private val userDataManager: UserDataManager,
) : BaseMatchRepository(scope, socketManager, betDao, matchDao, infoDao, userDataManager) {

    /**
     * 根據不同的條件，從api或是db(優先)取得賽事資料，如果從api來的話，拿到後會先存進資料庫內
     * @param playType : 一級導航欄
     * @param sportId : 二級導航欄
     * @param tournamentIdList : 聯賽id列表
     * @param page : 頁數
     * @param date : 0 -> All、其餘時間為該日期的start time
     * @param startTime : 時間區間起始，如果非早盤類型為0，早盤的"ALL"為隔日早上00:00:00
     * @param endTime : 時間區間結束，如果非早盤類型為0，早盤的"ALL"為隔日開始起算30日
     * */
    suspend fun getAllMatch(
        playType: Int,
        sportId: Int,
        tournamentIdList: List<Int>,
        prevPage: Int = INITIAL_PAGE -1,
        page: Int,
        date: Long,
        startTime: Long,
        endTime: Long,
        isForce: Boolean = false,  //是否刪除之前的資料
        loadMatchType: LoadMatchType,
    ): ApiResponseState {
        val cursor = if (loadMatchType == LoadMatchType.PREV_PAGE) {
//            "prevPage:$prevPage".logi("prevPageIssue")
            //向前查询， 需要取首场比赛
            if (playType == PlayType.EARLY.id) {
                matchDao.queryEarlyFirstMatch(tournamentIdList, date)
            } else {
                matchDao.queryFirstMatch(playType, tournamentIdList, date)
            }
        } else {
            //其他查询类型， 需要取最后一场比赛
            if (isForce) null else {
                if (playType == PlayType.EARLY.id) {
                    matchDao.queryEarlyLastMatch(tournamentIdList, date)
                } else {
                    matchDao.queryLastMatch(playType, tournamentIdList, date)
                }
            }
        }
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_MATCH,
        ) {
            Client.ListMatchReq.newBuilder().apply {
                this.sportId = sportId
                this.playType = playType
                //临时解决早盘和今日中，“全部”tab没有比赛数据的问题
                //联赛id改成了列表， 查“全部”数据时传空列表
                if (!tournamentIdList.contains(0)) {
                    tournamentIdList.forEach { this.addTournamentId(it) }
                }
                this.size = DEFAULT_MATCH_SIZE
                this.startTime = startTime
                this.endTime = endTime
                if (cursor != null) {
                    this.cursorMatchId = cursor.matchId
                    this.cursorMatchStartTime = cursor.basicInfo.startTime
                }
                this.reverse = loadMatchType == LoadMatchType.PREV_PAGE //判断是取上一页，还是下一页
            }.build()
        }

        if (resp.error == null && resp.data != null) {
            val matchFullData = resp.data!!.matchList.toRoomData()
            "新增比賽 tournamentIdList = $tournamentIdList matchId = ${matchFullData.match.map { it.matchId }} 進入資料庫".logi(
                HomeRepository::class.java.simpleName
            )

            if (playType == PlayType.EARLY.id) {
                //早盘
                val tournamentMatchRefs = resp.data!!.matchList.mapIndexed { index, match ->
                    EarlyTournamentMatchRef(
                        playType = playType,
                        tournamentIdList = tournamentIdList,
                        date = date,
                        matchId = match.matchId,
                        order = if (loadMatchType == LoadMatchType.PREV_PAGE) {
                            prevPage * 100 + index
                        } else {
                            page * 100 + index
                        }
                    )
                }

                val refIds =
                    matchDao.insertEarlyMatch(
                        tournamentMatchRefs = tournamentMatchRefs,
                        matches = matchFullData.match,
                        markets = matchFullData.markets,
                        selections = matchFullData.selections,
                        marketCrossRef = matchFullData.matchMarketCrossRefs,
                        marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
                        playType = playType,
                        tournamentIdList = tournamentIdList,
                        date = date,
                        isForce = isForce
                    )
                "New match data from api insert success : $refIds".logi(this::class.java.simpleName)

            } else {
                //其他playType
                val tournamentMatchRefs =
                    resp.data!!.matchList.mapIndexed { index, match ->
                        TournamentMatchRef(
                            playType = playType,
                            tournamentIdList = tournamentIdList,
                            page = page,
                            date = date,
                            matchId = match.matchId,
                            order = page * 100 + index
                        )
                    }

                val refIds =
                    matchDao.insertMatch(
                        tournamentMatchRefs = tournamentMatchRefs,
                        matches = matchFullData.match,
                        markets = matchFullData.markets,
                        selections = matchFullData.selections,
                        marketCrossRef = matchFullData.matchMarketCrossRefs,
                        marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
                        playType = playType,
                        tournamentIdList = tournamentIdList,
                        date = date,
                        isForce = isForce
                    )
                "New match data from api insert success : $refIds".logi(this::class.java.simpleName)

            }
            return ApiResponseState.Succeeded(resp.data!!.matchList)
        }
        return ApiResponseState.Failed(resp.error)
    }

    fun clearCurrentMatch(playType: Int, tournamentIdList: List<Int>, date: Long) {
        matchDao.deleteCurrentTournamentMatchRef(playType, tournamentIdList, date)
    }

    fun observeMatchChange(playType: Int, tournamentIdList: List<Int>): Flow<List<TournamentMatchRef>> {
        //觀察後端的500-1002（获取比赛列表）回傳
        return matchDao.observeMatchChange(playType, tournamentIdList)
    }

    /**
     * 观察早盘比赛
     */
    fun observeEarlyMatchChange(
        playType: Int,
        tournamentIdList: List<Int>
    ): Flow<List<EarlyTournamentMatchRef>> {
        //觀察後端的500-1002（获取比赛列表）回傳
        return matchDao.observeEarlyMatchChange(playType, tournamentIdList)
    }

    suspend fun queryMatchChange(playType: Int, tournamentIdList: List<Int>): List<TournamentMatchRef> =
        matchDao.queryMatchChange(playType, tournamentIdList)

    suspend fun queryEarlyMatchChange(playType: Int, tournamentIdList: List<Int>): List<EarlyTournamentMatchRef> =
        matchDao.queryEarlyMatchChange(playType, tournamentIdList)
}