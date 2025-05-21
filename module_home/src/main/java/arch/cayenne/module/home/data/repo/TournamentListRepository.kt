package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.TournamentDao
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.ui.fragment.TournamentListType
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class TournamentListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val tournamentDao: TournamentDao
) : BaseRepository() {

    suspend fun getAllTournaments(type: TournamentListType, sportId: Int): List<BaseTournamentData> {
        return if (type == TournamentListType.MORE) {
            tournamentDao.queryTournament()
        } else {
            getChampionTournament(sportId)
        }
    }

    private suspend fun getChampionTournament(sportId: Int): List<ChampionTournamentDataModel> { //先暫時用TournamentDataModel
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
            return res.data!!.outrightMatchOrBuilderList.map {
                ChampionTournamentDataModel(
                    id = it.tournamentId,
                    championMatchId = it.matchId,
                    sportId = it.sportId,
                    name = it.tournamentName,
                    simpleName = "",
                    icon = it.tournamentIcon,
                    weight = it.weight,
                    hot = it.hot,
                )
            }
        }
        return arrayListOf()
    }
}