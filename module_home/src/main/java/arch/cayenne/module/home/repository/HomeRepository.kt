package arch.cayenne.module.home.repository

import androidx.room.Transaction
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.toRoomData
import arch.cayenne.module.home.viewmodel.BaseGameListViewModel.Companion.DEFAULT_MATCH_SIZE
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class HomeRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val database: GameDatabase
) : BaseRepository() {
    private val sportDao = database.sportDao()
    private val tournamentDao = database.tournamentDao()

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

    suspend fun getAllMatch(playType: Int, sportId: Int, tournamentId: Int, size: Int, page: Int) : List<MatchWithMarkets> {
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
            }.build()
        }

        if (resp.error == null && resp.data != null) {
            val matchFullData = resp.data!!.matchList.toRoomData()
            database.matchDao().insertFullMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )
            return database.matchDao().getFullMatch()
        }
        return arrayListOf()
    }

    suspend fun observeBalance(): Flow<Long> = database.infoDao().observeBalance()
}