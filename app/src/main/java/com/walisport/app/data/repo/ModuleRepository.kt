package com.walisport.app.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentMatchRef
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.data.repo.HomeRepository
import com.walisport.app.IPreLoadHomeApi
import com.walisport.app.data.PreLoadDataModel
import com.walisport.app.data.toRoomData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModuleRepository(
    override val scope: CoroutineScope,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
    private val socketManager: WebSocketManager,
): BaseRepository() {
    private val TAG = this.javaClass.simpleName
    val matchDao = database.matchDao()
    val sportDao = database.sportDao()
    val tournamentDao = database.tournamentDao()

    fun preLoadHome() {
//        val httpClient = GlobalContext.get().get<HttpClient>(named("firstApi"))
        val api = httpClient.create(IPreLoadHomeApi::class.java)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
                request = {
                    api.postPreLoad(
                        mapOf(
                            "lang" to LanguageType.LANGUAGE_SIMPLE.value,
                            "oddType" to "0"
                        )
                    )
                },
                onSuccess = {
                    "response------>${it}".logd(TAG)
                    savePreLoadData(it)
                },
                onFailure = { code, msg, throwable ->
                    "response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    @Transaction
    private fun savePreLoadData(data: PreLoadDataModel) {
        try {
            scope.launch(Dispatchers.IO) {
                //新增sport進入Database
                val sportBeans = arrayListOf<SportBean>()
                data.statistical.forEach {  play ->
                    play.sportStatistical.forEachIndexed { index, sport ->
                        sportBeans.add(
                            SportBean(
                                sportId = sport.sportId,
                                sportName = sport.sportName,
                                matchCount = sport.matchCount?:0,
                                sportOrder = index,
                                type = play.playType.playTypeToShowType(),
                                date = 0L
                            )
                        )
                    }
                }
                sportDao.insert(sportBeans)

                //新增聯賽進入Database
                val playTypeId = data.statistical.firstOrNull()?.playType ?: PlayType.TODAY.id

                val tournamentList = mutableListOf<TournamentBean>()
                val refs = mutableListOf<SportTournamentCrossRef>().apply {
                    add(
                        SportTournamentCrossRef(
                            tournamentId = 0,
                            playType = playTypeId,
                            sportId = data.tournament.getOrNull(0)?.sportId ?: SportEnum.Soccer.id,
                            hot = false,
                            weight = Int.MAX_VALUE,
                            index = 0,
                            coordinateY = 0,
                            matchId = null,
                        )
                    )
                }
                data.tournament.forEachIndexed { index, tournament ->
                    tournamentList.add(
                        TournamentBean(
                            id = tournament.id,
                            name = tournament.name,
                            simpleName = tournament.simpleName,
                            icon = tournament.icon?:"",
                        )
                    )
                    refs.add(
                        SportTournamentCrossRef(
                            tournamentId = tournament.id,
                            playType = playTypeId,
                            sportId = tournament.sportId,
                            hot = tournament.hot,
                            weight = tournament.weight,
                            index = index+1,
                            coordinateY = 0,
                            matchId = null,
                        )
                    )
                }
                tournamentDao.insert(tournamentList)
                tournamentDao.insertSportTournamentCrossRefs(refs)

                val tournamentId = 0  //default沒給，只能預設為是全部聯賽
                val matchFullData = data.match.toRoomData()
                "新增比賽 tournamentId = ${tournamentId} matchId = ${matchFullData.match.map { it.matchId }} 進入資料庫".logi(
                    HomeRepository::class.java.simpleName)
                val tournamentMatchRefs = data.match.mapIndexed { index, match ->
                    TournamentMatchRef(
                        playType = playTypeId,
                        tournamentId = tournamentId,
                        page = 0,
                        date = 0,//default沒給，只能預設為是今日
                        matchId = match.matchId,
                        order = index
                    )
                }
                database.matchDao().insertMatch(
                    tournamentMatchRefs = tournamentMatchRefs,
                    matches = matchFullData.match,
                    markets = matchFullData.markets,
                    selections = matchFullData.selections,
                    marketCrossRef = matchFullData.matchMarketCrossRefs,
                    marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //开始连接服务器
    fun startSocket() {
        socketManager.connect("wss://betwavepro.ja700.com/fb-ws")
    }
}