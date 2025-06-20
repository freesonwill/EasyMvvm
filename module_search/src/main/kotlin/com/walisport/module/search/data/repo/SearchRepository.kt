package com.walisport.module.search.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.walisport.module.search.data.model.RecordBean
import com.walisport.module.search.data.model.SearchDailyMatchBean
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchTypeEnum
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class SearchRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
) : BaseRepository() {

    /** * 删除一条搜索记录
     * @param keyword 要删除的关键字
     */
    fun deleteOneRecord(keyword: String?) {
        val record = userDataManager.getValue(UserDataKey.KEY_RECORD, "")
        if (record.isNotEmpty()) {
            val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
            val list = getRecordList(record)
            for (item in list) {
                val id = item.uid
                if (id == uid) {
                    if (keyword!!.isNotEmpty()) {
                        item.record = item.record.replace(keyword, "")
                    }
                }
            }
            val json = Gson().toJson(list)
            userDataManager.setKeyValue(UserDataKey.KEY_RECORD, json)
        }
    }

    /** * 添加一条搜索记录
     * @param key 要添加的关键字
     */
    fun addOneRecord(key: String) {
        // 获取当前用户ID
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        // 获取搜索记录
        val record = userDataManager.getValue(UserDataKey.KEY_RECORD, "")

        run {
            if (record.isNotBlank()) getRecordList(record).toMutableList()
            else mutableListOf()
        }.apply {
            find { it.uid == uid }?.let { userRecord ->
                // 有用戶紀錄，
                // 無論有沒有該關鍵字都重新寫入一次，
                // 讓最新的關鍵字可以排在最前面
                userRecord.record = (
                        userRecord.record
                            .split(";")
                            .filter { it != key } + key
                        ).joinToString(";")
            } ?: run {
                // 無用戶相關紀錄直接新增
                add(RecordBean(uid, key))
            }

            userDataManager.setKeyValue(
                UserDataKey.KEY_RECORD,
                Gson().toJson(this)
            )
        }
    }

    /**
     * 删除本账户对应的所有搜索记录
     */
    fun deleteAllData() {
        val record = userDataManager.getValue(UserDataKey.KEY_RECORD, "")
        if (record.isNotEmpty()) {
            val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
            val list = getRecordList(record)
            for (item in list) {
                val iid = item.uid
                if (iid == uid) {
                    item.record = ""
                }
            }
            val json = Gson().toJson(list)
            userDataManager.setKeyValue(UserDataKey.KEY_RECORD, json)
        }
    }

    /**
     * 获取本账户的搜索记录
     * @return 返回一个字符串列表，包含本账户的搜索记录
     */
    fun getRecordByUID(): List<String> {
        val historyList: MutableList<String> = ArrayList()
        val record = userDataManager.getValue(UserDataKey.KEY_RECORD, "")
        if (record.isNotEmpty()) {
            val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
            val list = getRecordList(record)
            for (item in list) {
                val iid = item.uid
                if (iid == uid) {
                    val data = item.record
                    if (data.isNotEmpty()) {
                        val parts = data.split(";").filter { it.isNotEmpty() }
                        for (str in parts) {
                            historyList.add(str)
                        }
                    }
                }
            }
        }
        if (historyList.size > 20) {//限制最多读取最新的20条
            return historyList.takeLast(20)
        }
        return historyList
    }

    /**
     * 将JSON字符串转换为RecordBean列表
     * JSON格式 {"uid": 43213,"record":"衣服;男鞋;香蕉;苹果;红薯"}
     * @param value JSON字符串
     */
    private fun getRecordList(value: String): List<RecordBean> {
        val type = object : TypeToken<List<RecordBean>>() {}.type
        return Gson().fromJson(value, type)
    }

    suspend fun addCollect(matchId: Long): Boolean {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.AddCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.ADD_COLLECT
        ) {
            Client.AddCollectReq.newBuilder().apply {
                addMatchId(matchId)
            }.build()
        }
        return result.data?.success ?: false
    }

    suspend fun removeCollect(matchId: Long): Boolean {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.RemoveCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.REMOVE_COLLECT
        ) {
            Client.RemoveCollectReq.newBuilder().apply {
                addMatchId(matchId)
            }.build()
        }
        return result.data?.success ?: false
    }

    /** * 获取搜索结果
     * @param word 搜索关键词 / 聯賽ID / 球隊ID / 球員ID
     * @param type 搜索类型 （1-普通词 / 2-热门词 / 10-球员 / 11-球队 / 12-联赛）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码
     * @param size 每页大小
     * @param timeZone 时区偏移量，默认8小时
     */
    suspend fun getSearchResult(
        word: String,
        type: SearchTypeEnum = SearchTypeEnum.NORMAL_WORD,
        startTime: Long? = null,
        endTime: Long? = null,
        page: Int = 1,
        size: Int = 10,
        timeZone: Int = 8
    ): SearchResultBean {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.SearchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SEARCH,
        ) {
            Client.SearchReq.newBuilder().apply {
                this.word = word
                this.type = type.code
                startTime?.let { this.startTime = it }
                endTime?.let { this.endTime = it }
                this.page = page
                this.size = size
                this.timeZone = timeZone
            }.build()
        }

        when(SearchResultTypeEnum.fromCode(result.data?.type ?: 0)) {
            SearchResultTypeEnum.LIST -> {
                return SearchResultBean(
                    type = SearchResultTypeEnum.LIST,
                    dataList = listOfNotNull(
                        result.data?.dataList?.tournamentList?.let { SearchResultTournamentBean.fromList(it) },
                        result.data?.dataList?.teamList?.let { SearchResultTeamBean.fromList(it) },
                        result.data?.dataList?.playerList?.let { SearchResultPlayerBean.fromList(it) }
                    ).flatten()

                )
            }
            SearchResultTypeEnum.PLAYER -> {
                return SearchResultBean(
                    type = SearchResultTypeEnum.PLAYER,
                    directData = result.data?.player?.let { SearchResultPlayerBean.from(it) },
                    matchTotal = result.data?.matchTotal ?: 0,
                    matches = result.data?.matchesList?.let { SearchMatchBean.fromList(it) },
                    dailyCount = result.data?.dailyCountList?.let { list ->
                        SearchDailyMatchBean.fromList(list)
                    }
                )
            }
            SearchResultTypeEnum.TEAM -> {
                return SearchResultBean(
                    type = SearchResultTypeEnum.TEAM,
                    directData = result.data?.team?.let { SearchResultTeamBean.from(it) },
                    matchTotal = result.data?.matchTotal ?: 0,
                    matches = result.data?.matchesList?.let { SearchMatchBean.fromList(it) },
                    dailyCount = result.data?.dailyCountList?.let { list ->
                        SearchDailyMatchBean.fromList(list)
                    }
                )
            }
            SearchResultTypeEnum.TOURNAMENT -> {
                return SearchResultBean(
                    type = SearchResultTypeEnum.TOURNAMENT,
                    directData = result.data?.tournament?.let { SearchResultTournamentBean.from(it) },
                    matchTotal = result.data?.matchTotal ?: 0,
                    matches = result.data?.matchesList?.let { SearchMatchBean.fromList(it) },
                    dailyCount = result.data?.dailyCountList?.let { list ->
                        SearchDailyMatchBean.fromList(list)
                    }
                )
            }
            else -> { return SearchResultBean(type = SearchResultTypeEnum.NONE) }
        }
    }

    /**
     * 获取搜索推荐词
     * @param keyword 搜索关键词
     */
    suspend fun getSearchRecommend(keyword: String? = ""): List<String> {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.SearchRecommendResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SEARCH_RECOMMEND,
        ) {
            Client.SearchRecommendReq.newBuilder().apply {
                word = keyword
            }.build()
        }
        return resp.data?.recommendList?.toList() ?: listOf()
    }

    /**
     * 获取搜索热词
     * @return 返回热词列表
     */
    suspend fun getSearchHotWord(): List<String> {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.SearchHotWordResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SEARCH_HOT_WORD,
        ) {
            Client.SearchHotWordReq.newBuilder().build()
        }
        return resp.data?.wordList?.toList() ?: listOf()
    }
}