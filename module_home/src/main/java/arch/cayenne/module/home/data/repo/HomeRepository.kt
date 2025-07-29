package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.HomeSelectedBean
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val database: GameDatabase
) : BaseRepository() {
    private val sportDao = database.sportDao()
    private val tournamentDao = database.tournamentDao()
    private val matchDao = database.matchDao()
    private val homeSelectedDao = database.homeSelectedDao()
    private val infoDao = database.infoDao()

    fun observeSportsMatchCount() = sportDao.observeSportsMatchCount(filter = SportType.entries.map { it.id })
    fun observeTenTournaments() = tournamentDao.observeTournamentWithLimit()

    fun observeLanguageChange() = userDataManager.observe<String>(UserDataKey.KEY_LANGUAGE)

    suspend fun observeLoginChange() = infoDao.observeIsLogin()

    suspend fun getTournament(playTypeId: Int, sportId: Int, tournamentId: Int): TournamentDataModel? = tournamentDao.queryTournament(playTypeId, sportId, tournamentId)

    @Transaction
    suspend fun getSportStatistical(): ApiResponseState = withContext(scope.coroutineContext) {
        "取得球類資料(500-1000)".logi(HomeRepository::class.java.simpleName)
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.StatisticalResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.STATISTICAL,
        ) {
            Client.StatisticalReq.newBuilder().build()
        }
        return@withContext if (res.error == null && res.data != null) {
            saveSports(res.data!!)
        } else {
            ApiResponseState.Failed(error = res.error)
        }
    }
    private suspend fun saveSports(data: Client.StatisticalResp): ApiResponseState.Succeeded<*> {
        val dataList = mutableListOf<SportBean>()
        data.statisticalList.forEach { play ->
            play.sportStatisticalList.forEachIndexed { index, sport ->
                dataList.add(
                    sportDao.getSportById(sport.sportId, play.playType.playTypeToShowType())?.copy(
                        matchCount = sport.matchCount
                    ) ?: run {
                        SportBean(
                            sportId = sport.sportId,
                            sportName = sport.sportName,
                            matchCount = sport.matchCount,
                            sportOrder = index,
                            type = play.playType.playTypeToShowType(),
                            date = 0L
                        )
                    }
                )
            }
        }
        sportDao.insert(dataList)
        sportDao.deleteMissing(dataList.map { it.sportId })
        return ApiResponseState.Succeeded(dataList)
    }

    @Transaction
    suspend fun getTenTournaments(playType: Int, sportId: Int): ApiResponseState = withContext(scope.coroutineContext) {
        "取得联赛资料 playType = $playType, sportId = $sportId".logi(this@HomeRepository::class.java.simpleName)
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
        return@withContext if (res.error == null && res.data != null) {
            saveTournaments(playType, sportId, res.data!!)
        } else {
            ApiResponseState.Failed(res.error)
        }
    }

    private suspend fun saveTournaments(
        playType: Int,
        sportId: Int,
        data: Client.ListTournamentResp
    ): ApiResponseState.Succeeded<*> {
        val tournamentList = mutableListOf<TournamentBean>()
        val refs = mutableListOf<SportTournamentCrossRef>().apply {
            add(
                tournamentDao.getSportTournamentCrossRef(playType, sportId, 0) ?: run {
                    SportTournamentCrossRef(
                        tournamentId = 0,
                        playType = playType,
                        sportId = sportId,
                        hot = false,
                        weight = Int.MAX_VALUE,
                        index = 0,
                        coordinateY = 0,
                        matchId = null,
                    )
                }
            )
        }
        data.tournamentList.forEachIndexed { index, tournament ->
            tournamentList.add(
                TournamentBean(
                    id = tournament.id,
                    name = tournament.name,
                    simpleName = tournament.simpleName,
                    icon = tournament.icon,
                )
            )
            refs.add(
                tournamentDao.getSportTournamentCrossRef(playType, sportId, tournament.id)?.copy(
                hot = tournament.hot,
                weight = tournament.weight,
                index = index+1,
                ) ?: run {
                    SportTournamentCrossRef(
                        tournamentId = tournament.id,
                        playType = playType,
                        sportId = sportId,
                        hot = tournament.hot,
                        weight = tournament.weight,
                        index = index+1,
                        coordinateY = 0,
                        matchId = null,
                    )
                }
            )
        }

        tournamentDao.insert(tournamentList)
        tournamentDao.insertSportTournamentCrossRefs(refs)
        tournamentDao.deleteMissing(sportId, playType, refs.map { it.tournamentId })
        return ApiResponseState.Succeeded(tournamentList)
    }

    suspend fun clearAllCache() {
        homeSelectedDao.clearAllHomeSelectedData()
        matchDao.clearAllMatch()
        tournamentDao.clearAllTournaments()
        tournamentDao.clearAllSportTournamentCrossRef()
        sportDao.clearSportBean()
    }

    suspend fun getRecently31MatchScheduleCount(sportId: Int, playType: Int,tournamentId:Int, timeZone: Int = 8): ApiResponseState = withContext(scope.coroutineContext) {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.Recently31MatchScheduleCountResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.RECENTLY_31_MATCH_SCHEDULE_COUNT,
        ) {
            Client.Recently31MatchScheduleCountReq.newBuilder().apply {
                this.sportId = sportId
                this.playType = playType
                this.tournamentId = tournamentId
                this.timeZone = timeZone
            }.build()
        }
        return@withContext if (res.error == null && res.data != null) {
            ApiResponseState.Succeeded(res.data!!.dailyCountList)
        } else {
            ApiResponseState.Failed(res.error)
        }
    }

    private fun getCurrentHomeSelectedData(playType: Int): HomeSelectedBean? = homeSelectedDao.queryHomeSelectedData(playType)

    suspend fun updateSelectedSportId(playType: Int, sportId: Int) {
        val bean = getCurrentHomeSelectedData(playType)?.copy(sportId = sportId) ?: HomeSelectedBean(playType, sportId, 0 )
        homeSelectedDao.insert(bean)
    }
    suspend fun getCurrentSelectedSportId(playType: Int): Int? = getCurrentHomeSelectedData(playType)?.sportId

    suspend fun updateSelectedTournamentId(playType: Int, tournamentId: Int) {
        getCurrentHomeSelectedData(playType)?.copy(tournamentId = tournamentId)?.apply {
            homeSelectedDao.insert(this)
        }
    }
    suspend fun getCurrentSelectedTournamentId(playType: Int): Int? = getCurrentHomeSelectedData(playType)?.tournamentId

    suspend fun updateSelectedDate(showType: ShowType, sportId: Int, date: Long) {
        sportDao.getSportById(sportId, showType)?.copy(date = date)?.apply {
            sportDao.insert(this)
        }
    }
    suspend fun getCurrentSelectedDate(showType: ShowType, sportId: Int): Long? = sportDao.getSportById(sportId, showType)?.date

    suspend fun updateScrollCoordinate(
        playTypeId: Int,
        sportId: Int,
        tournamentId: Int,
        coordinate: Int
    ) {
        tournamentDao.updateRefCoordinate(playTypeId, sportId, tournamentId, coordinate)
    }
    suspend fun getCurrentPageCoordinate(playTypeId: Int, sportId: Int, tournamentId: Int) : Int? = tournamentDao.getSportTournamentCrossRef(playTypeId, sportId, tournamentId)?.coordinateY
}