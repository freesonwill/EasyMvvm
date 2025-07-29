package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.TournamentDao
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.constants.PlayType
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class TournamentListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val tournamentDao: TournamentDao,
    private val infoDao: InfoDao,
) : BaseRepository() {

    suspend fun getChampionTournament(sportId: Int): ApiResponseState { //先暫時用TournamentDataModel
        val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListOutrightMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LIST_OUTRIGHT_MATCH,
        ) {
            Client.ListOutrightMatchReq.newBuilder().apply {
                this.sportId = sportId
            }.build()
        }
        if (res.error == null && res.data != null) {
            saveTournaments(
                playType = PlayType.CHAMPION.id,
                sportId = sportId,
                data = res.data!!
            )
            return ApiResponseState.Succeeded(
                res.data!!.outrightMatchOrBuilderList.map {
                    ChampionTournamentDataModel(
                        id = it.tournamentId,
                        championMatchId = it.matchId,
                        sportId = it.sportId,
                        playTypeId = PlayType.CHAMPION.id,
                        name = it.tournamentName,
                        simpleName = "",
                        icon = it.tournamentIcon,
                        weight = it.weight,
                        hot = it.hot,
                    )
                }
            )
        }
        return ApiResponseState.Failed(res.error)
    }

    private suspend fun saveTournaments(
        playType: Int,
        sportId: Int,
        data: Client.ListOutrightMatchResp
    ): ApiResponseState.Succeeded<*> {
        val tournamentList = mutableListOf<TournamentBean>()
        val refs = mutableListOf<SportTournamentCrossRef>()
        data.outrightMatchOrBuilderList.forEachIndexed { index, tournament ->
            tournamentList.add(
                TournamentBean(
                    id = tournament.tournamentId,
                    name = tournament.tournamentName,
                    simpleName = tournament.tournamentName,
                    icon = tournament.tournamentIcon,
                )
            )
            refs.add(
                SportTournamentCrossRef(
                    tournamentId = tournament.tournamentId,
                    playType = playType,
                    sportId = sportId,
                    hot = tournament.hot,
                    weight = tournament.weight,
                    index = index+1,
                    coordinateY = 0,
                    matchId = tournament.matchId,
                )
            )
        }

        tournamentDao.insert(tournamentList)
        tournamentDao.insertSportTournamentCrossRefs(refs)
        tournamentDao.deleteMissing(sportId, playType, refs.map { it.tournamentId })
        return ApiResponseState.Succeeded(tournamentList)
    }

    suspend fun queryTournaments(playTypeId: Int, sportId: Int) = tournamentDao.queryTournaments(playTypeId, sportId)
    suspend fun queryChampionTournaments(sportId: Int) = tournamentDao.queryChampionTournaments(PlayType.CHAMPION.id, sportId)

    suspend fun observeLoginChange() = infoDao.observeIsLogin()
}