package arch.cayenne.module.home.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportCategory
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentCategory
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.socket.WebSocketManager
import arch.cayenne.lib.socket.data.ApiCode
import arch.cayenne.lib.socket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.Market
import arch.cayenne.module.home.data.MarketDetail
import arch.cayenne.module.home.data.Match
import arch.cayenne.module.home.data.MatchBasicInfo
import arch.cayenne.module.home.data.MatchLiveInfo
import arch.cayenne.module.home.data.Selection
import arch.cayenne.module.home.viewmodel.BaseGameListViewModel.Companion.DEFAULT_MATCH_SIZE
import galaxy.client.proto.Client
import galaxy.common.proto.Common
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

    suspend fun getTenTournaments(playType: Int, sportId: Int): List<TournamentDataModel>? {
        //先從DB拿取
        val queryResult = tournamentCategoryDao.queryTournamentWithLimit(playType, sportId, 10)
        if (queryResult.isNotEmpty()) {
            return queryResult
        }
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
        tournamentDao.insert(tournamentList)
        tournamentCategoryDao.insert(tournamentCategoryList)
        return tournamentCategoryDao.queryTournamentWithLimit(playType, sportId, 10)
    }

    suspend fun getAllMatch(playType: Int, sportId: Int, tournamentId: Int, size: Int, page: Int) : List<Match> {
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
            return resp.data!!.matchList.map { match ->
                Match(
                    matchId = match.matchId,
                    collect = match.collect,
                    basicInfo = transformMatchBasicInfo(match.basicInfo),
                    market = transformMarket(match.marketList)
                )
            }.toList()
        }
        return arrayListOf()
    }

    private fun transformMatchBasicInfo(originalData: Common.MatchBasicInfo) : MatchBasicInfo {
        return MatchBasicInfo(
            matchId = originalData.matchId,
            matchName = originalData.matchName,
            homeTeam = originalData.homeTeam,
            homeTeamId = originalData.homeTeamId,
            homeTeamIcon = originalData.homeTeamIcon,
            awayTeam = originalData.awayTeam,
            awayTeamId = originalData.awayTeamId,
            awayTeamIcon = originalData.awayTeamIcon,
            startTime = originalData.startTime,
            status = originalData.status,
            tournamentId = originalData.tournamentId,
            tournamentName = originalData.tournamentName,
            tournamentShortName = originalData.tournamentShortName,
            tournamentIcon = originalData.tournamentIcon,
            sportId = originalData.sportId,
            sportName = originalData.sportName,
            liveInfo = transformLiveInfo(originalData.liveInfo),
            betStop = originalData.betStop,
            tournamentHot = originalData.tournamentHot,
            tournamentWeight = originalData.tournamentWeight
        )
    }

    private fun transformLiveInfo(originalData: Common.MatchLiveInfo) : MatchLiveInfo {
        return MatchLiveInfo(
            clock = originalData.clock,
            rollClock = originalData.rollClock,
            period = originalData.period,
            score = originalData.score,
            liveVideo = originalData.liveVideo,
            charRoom = originalData.chatRoom,
            viewerCount = originalData.viewerCount,
            clockModified = originalData.clockModified
        )
    }

    private fun transformMarket(originalData: List<Common.Market>) : List<Market> {
        return originalData.map { market ->
            Market(
                marketId = market.marketId,
                marketName = market.marketName,
                marketDetail = transformMarketDetail(market.marketDetailList),
                status = market.status
            )
        }.toList()
    }

    private fun transformMarketDetail(originalData: List<Common.MarketDetail>) : List<MarketDetail> {
        return originalData.map { marketDetail ->
            MarketDetail(
                specifier = marketDetail.specifier,
                selection = transformSelection(marketDetail.selectionList),
                active = marketDetail.active,
                parlay = marketDetail.parlay
            )
        }.toList()
    }

    private fun transformSelection(originalData: List<Common.Selection>) : List<Selection> {
        return originalData.map { selection ->
            Selection(
                selectionId = selection.selectionId,
                name = selection.name,
                shortName = selection.shortName,
                odds = selection.odds,
                active = selection.active,
                parlay = selection.parlay
            )
        }.toList()
    }
}