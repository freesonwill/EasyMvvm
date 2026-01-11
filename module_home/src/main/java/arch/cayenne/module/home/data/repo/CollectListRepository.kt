package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.CollectListDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.dao.SportLoginInfoDao
import arch.cayenne.lib.database.entity.CollectListBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.model.CollectMatchRef
import arch.cayenne.module.home.data.model.toRoomData
import arch.cayenne.module.home.utils.DateUtils
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.concurrent.fixedRateTimer

class CollectListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val betDao: BetDao,
    private val matchDao: MatchDao,
    private val infoDao: InfoDao,
    private val collectListDao: CollectListDao,
    private val sportLoginInfoDao: SportLoginInfoDao,
    private val userDataManager: UserDataManager,
) : BaseMatchRepository(
    scope,
    socketManager,
    betDao,
    matchDao,
    infoDao,
    sportLoginInfoDao,
    userDataManager
) {

    private val collectMatchChange by lazy { MutableStateFlow<Map<Long, CollectMatchRef>>(hashMapOf()) }  //CollectMatchCrossRef

    suspend fun getCollectData(
        page: Int,
        prevPage:Int,//向前查询时使用的页码
        reverse: Boolean = false, //true-上页，false-下页
        isForce: Boolean = false
    ): ApiResponseState {
        val last =
            if (isForce) null else collectMatchChange.value.maxByOrNull { it.value.order }?.value
        val first = collectMatchChange.value.minByOrNull { it.value.order }?.value
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LISt_COLLECT,
        ) {
            if (reverse) {
                Client.ListCollectReq.newBuilder().apply {
                    if (first != null) {
                        this.cursorMatchId = first.matchId
                        this.cursorMatchStartTime = first.startTime
                    } else {
                        this.cursorMatchId = 0
                        this.cursorMatchStartTime =
                            DateUtils.getMidnightTimeStamp(System.currentTimeMillis())
                    }
                    this.size = DEFAULT_MATCH_SIZE
                    this.reverse = true
                }.build()
            }
            else {
                Client.ListCollectReq.newBuilder().apply {
                    if (last != null) {
                        this.cursorMatchId = last.matchId
                        this.cursorMatchStartTime = last.startTime
                    } else {
                        this.cursorMatchId = 0
                        this.cursorMatchStartTime =
                            DateUtils.getMidnightTimeStamp(System.currentTimeMillis())
                    }
                    this.size = DEFAULT_MATCH_SIZE
                    this.reverse = false
                }.build()
            }
        }
        if (resp.error == null && resp.data != null) { //curosr506605 1758204000000 0
            val matchFullData = resp.data!!.matchList.toRoomData()
            matchDao.insertMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )

            val pageNo = if (reverse) {
                prevPage
            } else {
                page
            }

            val map = resp.data!!.matchList.mapIndexed { index, match ->
                match.matchId to CollectMatchRef(
                    match.matchId,
                    match.basicInfo.startTime,
                    pageNo,
                    pageNo * 100 + index
                )
            }.toMap()

            if (reverse) {
                //向前查询时， 新数据加在列表前边
                collectMatchChange.value = map + collectMatchChange.value
            } else {
                //向后查询时，新数据加载列表后边
                collectMatchChange.value += map
            }
            return ApiResponseState.Succeeded(resp.data!!.matchList)
        }
        return ApiResponseState.Failed(resp.error)
    }

    fun clearCurrentMatch() {
        collectMatchChange.value = emptyMap()
    }

    @Transaction
    suspend fun removeMatchCollect(item: MatchWithMarkets): ApiResponseState {
        val resp = matchCollect(item, false)
        if (resp is ApiResponseState.Succeeded<*>) {
            collectMatchChange.value = collectMatchChange.value - item.match.matchId
        }
        return resp
    }

    fun observeMatchChange(): Flow<Map<Long, CollectMatchRef>> = collectMatchChange

    override fun deleteMissingMatch(matchIds: List<Long>) {
        super.deleteMissingMatch(matchIds)
        matchIds.forEach { collectMatchChange.value = collectMatchChange.value - it }
    }

    /**
     * 插入收藏的matchId
     * */
    fun insertCollectList(list: List<CollectMatchRef>) {
        if (list.isEmpty()) {
            return
        }
        val collectList: List<CollectListBean> = list.map {
            CollectListBean(
                it.matchId,
                it.startTime,
                it.page,
                it.order
            )
        }.toList()
        collectListDao.deleteAll()
        collectListDao.insertCollects(collectList)
    }

    fun deleteCollectList() {
        collectListDao.deleteAll()
    }

    /**
     * 获取保存的收藏matchId
     * */
    fun getCollectList(): List<CollectMatchRef> {

        val list: List<CollectListBean> = collectListDao.getAllCollectList()

        return list.map { CollectMatchRef(it.matchId, it.startTime, it.page, it.order) }.toList()
    }

    /**
     * 进入收藏页需要清空数据
     * 多次重复进入收藏后CollectMatchange数据没有清空，导致 getCollectData方法中last数据不为空，
     * 错误的选择了更多加载而不是初始化获取数据
     * */
    fun clear() {
        collectMatchChange.value = mapOf()
    }

}