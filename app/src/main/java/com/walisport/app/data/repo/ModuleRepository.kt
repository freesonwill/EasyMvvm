package com.walisport.app.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.LanguageType
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.UserDataDao
import arch.cayenne.lib.database.entity.AvatarEmbedded
import arch.cayenne.lib.database.entity.CurrencyBean
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentMatchRef
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IAccount
import arch.cayenne.lib.http._interface.IConfig
import arch.cayenne.lib.http.data.CurrencyInfo
import arch.cayenne.lib.http.data.ProfileInfo
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.data.repo.HomeRepository
import com.blankj.utilcode.util.GsonUtils
import com.walisport.app.BuildConfig
import com.walisport.app.IPreLoadHomeApi
import com.walisport.app.data.PreloadDataModel
import com.walisport.app.data.toRoomData
import com.walisport.app.ui.fragment.SplashFragment.UserConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ModuleRepository(
    override val scope: CoroutineScope,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
    private val mockHttpClient: HttpClient,
    private val socketManager: WebSocketManager,
    private val preloadResultChange: MutableStateFlow<PreloadEnum>,
    private val manager: UserDataManager,
): BaseRepository() {
    val matchDao = database.matchDao()
    val sportDao = database.sportDao()
    val tournamentDao = database.tournamentDao()

    private val users by lazy {
        GsonUtils.fromJson(
            BuildConfig.users,
            Array<UserConfig>::class.java
        )
    }
    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        users.filter { it.name.startsWith("qatest") }
            .map { it.uid to it.token }
            .let { it[Random.nextInt(it.size)] }
        //Pair(55468822, "NTU0Njg4MjJfMTc1MTM1NTAxMDI3MDppUjNheWVyczZ4S3dyVEFX") //固定uid,token时放开
    } else {
        Pair(55468810, "NTU0Njg4MTBfMTc2NDk5NTk4NDgyNTpqOVVxOEVxeWRQRFF6V2Iy")
    }

    fun initUidToken() {
        "manager.BUILD_TIME:${
            manager.getValue(
                UserDataKey.KEY_BUILD_TIME,
                ""
            )
        },BuildConfig.BUILD_TIME:${arch.cayenne.lib.common.BuildConfig.BUILD_TIME}".logd(TAG)
        val uid = manager.getValue(UserDataKey.KEY_UID, -1).let {
            if (it == -1) pair.first else it
        }
        val token = manager.getValue(UserDataKey.KEY_TOKEN, "").let {
            it.ifEmpty { pair.second }
        }
        val name = users.find { it.uid == uid }?.name
        "initUidToken name:${name}, uid:$uid, token:$token".logd(TAG)
        saveUserData(uid, token)
    }

    private fun saveUserData(uid: Int, token: String) {
        manager.setKeyValue(UserDataKey.KEY_UID, uid)
        manager.setKeyValue(UserDataKey.KEY_TOKEN, token)
    }

    fun getProfileInfo() {
        val api = mockHttpClient.create(IAccount::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = {
                    api.profileInfo()
                },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        saveProfileInfo(resp.data)
                    }
                },
                onFailure = { code, msg, throwable ->
                    "response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    fun saveProfileInfo(profileInfo: ProfileInfo) {
        database.userDataDao().insert(
            UserDataBean(
                id = profileInfo.id,
                name = profileInfo.name,
                avatar = AvatarEmbedded(
                    url = profileInfo.avatar.url,
                    thumbhash = profileInfo.avatar.thumbhash
                ),
                registerTime = profileInfo.registerTime,
                vipLevel = profileInfo.vipLevel,
                balanceTotal = profileInfo.balanceTotal,
                balanceWallet = profileInfo.balanceWallet,
                currentBetAmount = profileInfo.currentBetAmount,
                requiredBetAmount = profileInfo.requiredBetAmount,
                vipStage = profileInfo.vipStage,
                nicknameChangeCount = profileInfo.nicknameChangeCount
            )
        )
    }

    fun getCurrencyConfig() {
        val api = mockHttpClient.create(IConfig::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = {
                    api.currency()
                },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        saveCurrencyConfig(resp.data)
                    }
                },
                onFailure = { code, msg, throwable ->
                    "response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    private fun saveCurrencyConfig(data: List<CurrencyInfo>) {
        database.currencyConfigDao().insert(
            data.map {
                CurrencyBean(
                    id = it.id,
                    virtual = it.virtual,
                    rate = it.rate,
                    unit = it.unit,
                    name = it.name,
                    ccy = it.ccy,
                )
            }
        )
    }


    fun preLoadHome() {
        val api = httpClient.create(IPreLoadHomeApi::class.java)
        val uid = manager.getValue(UserDataKey.KEY_UID, -1)
        val token = manager.getValue(UserDataKey.KEY_TOKEN, "")
        val lang = manager.getValue(UserDataKey.KEY_LANGUAGE, LanguageType.LANGUAGE_SIMPLE.value)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
                request = {
                    api.preLoad(
                        token = token,
                        uid = uid,
                        language = lang,
                    )
                },
                onSuccess = {
                    "response------>${it}".logd(TAG)
                    savePreLoadData(it)
                    preloadResultChange.value = PreloadEnum.SUCCESS
                },
                onFailure = { code, msg, throwable ->
                    "response------>$code,$msg,$throwable".loge(TAG)
                    preloadResultChange.value = PreloadEnum.FAILURE
                }
            )
        }
    }

    @Transaction
    private fun savePreLoadData(data: PreloadDataModel) {
        scope.launch(Dispatchers.IO) {
            try {
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
                val tournamentIdList = listOf(0)  //default沒給，只能預設為是全部聯賽
                val matchFullData = data.match.toRoomData()
                "新增比賽 tournamentId = ${tournamentIdList} matchId = ${matchFullData.match.map { it.matchId }} 進入資料庫".logi(
                    HomeRepository::class.java.simpleName)
                val tournamentMatchRefs = data.match.mapIndexed { index, match ->
                    TournamentMatchRef(
                        playType = playTypeId,
                        tournamentIdList = tournamentIdList,
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

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    //开始连接服务器
    fun startSocket() {
        socketManager.connect("wss://betwavepro.ja700.com/fb-ws")
    }
}

