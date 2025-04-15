package arch.cayenne.module.home.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportCategory
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentCategory
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class HomeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val database: GameDatabase
) : BaseRepository() {
    private val sportDao = database.sportDao()
    private val sportCategoryDao = database.sportCategoryDao()
    private val tournamentCategoryDao = database.tournamentCategoryDao()
    private val tournamentDao = database.tournamentDao()

    suspend fun getSportStatistical(playType: Int): List<SportCategory>? {
        //先從DB拿取
        val queryResult = sportCategoryDao.querySportsMatchCount(playType)
        if (queryResult.isNotEmpty()) {
            return queryResult
        }
        //從API拿取
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.StatisticalResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.STATISTICAL,
        ) {
            Client.StatisticalReq.newBuilder().build()
        }
        return if (res.error == null && res.data != null) {
            return saveSports(playType, res.data!!)
        } else {
            res.error
            null
        }

    }
    private fun saveSports(playType: Int, data: Client.StatisticalResp): List<SportCategory> {
        val sportMap = hashMapOf<Int, SportBean>()
        val categoryList = arrayListOf<SportCategory>()
        data.statisticalList.forEach { play ->
            play.sportStatisticalList.forEachIndexed { index, sport ->
                val bean = SportBean(
                    sportId = sport.sportId,
                    sportName = sport.sportName,
                )
                sportMap[bean.sportId] = bean
                val category = SportCategory(
                    sportId = sport.sportId,
                    playType = play.playType,
                    matchCount = sport.matchCount,
                    sportOrder = index
                )
                categoryList.add(category)
            }
        }
        sportDao.insert(sportMap.map{ it.value }.toList())
        sportCategoryDao.insert(categoryList)
        return sportCategoryDao.querySportsMatchCount(playType)
    }

    suspend fun getAllTournaments(playType: Int, sportId: Int): List<TournamentCategory>? {
        //先從DB拿取
        val queryResult = tournamentCategoryDao.queryTournamentBySportId(playType, sportId)
        if (queryResult.isNotEmpty()) {
            return queryResult
        }
        //從API拿取
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListTournamentResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.Tournament,
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

    private fun saveTournaments(playType: Int, sportId: Int, data: Client.ListTournamentResp): List<TournamentCategory> {
        val tournamentList = arrayListOf<TournamentBean>()
        val tournamentCategoryList = arrayListOf<TournamentCategory>()
        data.tournamentList.forEach { tournament ->
            tournamentList.add(
                TournamentBean(
                    id = tournament.id,
                    name = tournament.name,
                    simpleName = tournament.simpleName,
                    icon = tournament.icon,
                )
            )
            tournamentCategoryList.add(
                TournamentCategory(
                    tournamentId = tournament.id,
                    sportId = sportId,
                    playType = playType,
                    hot = tournament.hot,
                    weight = tournament.weight
                )
            )
        }
        tournamentDao.insert(tournamentList).isNotEmpty()
        tournamentCategoryDao.insert(tournamentCategoryList).isNotEmpty()
        return tournamentCategoryList
    }

    suspend fun getAllMatch(playType: Int, sportId: Int, tournamentId: Int) {
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_MATCH,
        ) {
            Client.ListMatchReq.newBuilder().apply {
                this.sportId = sportId
                this.playType = playType
                this.tournamentId = tournamentId
                this.page = 1
                this.size = 10
            }.build()
        }
        val l = res.data
    }
}