package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.constants.SportType
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val database: GameDatabase
) : BaseRepository() {
    private val sportDao = database.sportDao()
    private val tournamentDao = database.tournamentDao()
    private val matchDao = database.matchDao()

    fun observeSportsMatchCount() = sportDao.observeSportsMatchCount(filter = SportType.entries.map { it.id })
    fun observeTenTournaments(playType: Int, sportId: Int) = tournamentDao.observeTournamentWithLimit(playType = playType, sportId = sportId, limit = 10)

    @Transaction
    suspend fun getSportStatistical(): ApiResponseState = withContext(scope.coroutineContext) {
        //從API拿取
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
                val bean = SportBean(
                    sportId = sport.sportId,
                    sportName = sport.sportName,
                    matchCount = sport.matchCount,
                    sportOrder = index,
                    type = ShowType.HOME
                )
                dataList.add(bean)
            }
        }
        sportDao.insert(dataList)
        sportDao.deleteMissing(dataList.map { it.sportId })
        return ApiResponseState.Succeeded(dataList)
    }

    @Transaction
    suspend fun getTenTournaments(playType: Int, sportId: Int): ApiResponseState = withContext(scope.coroutineContext) {
        clearMatchCache()
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
        }
        tournamentDao.insert(tournamentList)
        tournamentDao.deleteMissing(tournamentList.map { it.id })
        return ApiResponseState.Succeeded(tournamentList)
    }


    // HomeRepository.kt
    fun getTournamentById(
        tournamentId: Int
    ): TournamentDataModel? {
        return tournamentDao.getTournamentById(tournamentId)
    }

    private fun clearMatchCache() {
        matchDao.clearAllMatch()
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
}