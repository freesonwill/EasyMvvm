package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.TournamentDao
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.constants.PlayType
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class TournamentListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val tournamentDao: TournamentDao,
    private val infoDao: InfoDao,
    private val userDataManager: UserDataManager,
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
            return saveTournaments(
                playType = PlayType.CHAMPION.id,
                sportId = sportId,
                data = res.data!!
            )
        }
        return ApiResponseState.Failed(res.error)
    }

    @Transaction
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
        return ApiResponseState.Succeeded(queryChampionTournaments(sportId))
    }

    suspend fun queryTournaments(playTypeId: Int, sportId: Int) = tournamentDao.queryTournaments(playTypeId, sportId)
    suspend fun queryChampionTournaments(sportId: Int) = tournamentDao.queryChampionTournaments(PlayType.CHAMPION.id, sportId)

    /**
     * 观察用户令牌的变化。
     *
     * 此方法通过监听用户数据管理器中存储的用户令牌（KEY_TOKEN），
     * 并将其转换为一个布尔值流，表示令牌是否存在且非空。
     *
     * @return 一个 `Flow<Boolean>`，当令牌存在且非空时发射 `true`，否则发射 `false`。
     */
    suspend fun observeUserToken(): Flow<Boolean> {
        return userDataManager.observe<String>(UserDataKey.KEY_TOKEN).transform {
            emit(it.isNotEmpty())
        }
    }
}