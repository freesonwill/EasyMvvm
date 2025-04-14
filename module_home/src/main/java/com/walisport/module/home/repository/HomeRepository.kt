package com.walisport.module.home.repository

import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib.database.GameDatabase
import com.walisport.lib.database.entity.SportBean
import com.walisport.lib.database.entity.SportCategory
import com.walisport.lib.database.entity.TournamentBean
import com.walisport.lib.database.entity.TournamentCategory
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.extension.sendAndWaitProtoMessageResponse
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

    suspend fun getAllStatistical() : Boolean {
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
            false
        }
    }
    private fun saveSports(data: Client.StatisticalResp): Boolean {
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
                    gameType = play.playType,
                    matchCount = sport.matchCount,
                    sportOrder = index
                )
                categoryList.add(category)
            }
        }

        return sportDao
                .insert(sportMap.map{ it.value }.toList())
                .isNotEmpty() &&
               sportCategoryDao
                .insert(categoryList)
                .isNotEmpty()
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

    fun getSportStatistical(playType: Int): List<SportCategory> = sportCategoryDao.querySportsMatchCount(playType)

    fun getDefaultSport(playType: Int): Int = sportCategoryDao.getDefaultSportId(playType)
}