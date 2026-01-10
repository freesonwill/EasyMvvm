package com.walisport.module.search.data.repo

import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.GameSupplierDataModel
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
import com.walisport.module.search.data.transformer.SearchTransformer.toSearchResultBean
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val database: GameDatabase
) : BaseRepository() {

    private val infoDao = database.infoDao()

    /** * 监听登录状态变化 */
    fun observeLoginChange(): Flow<Boolean> {
        return userDataManager.observe<String>(UserDataKey.KEY_TOKEN).transform {
            emit(it.isNotEmpty())
        }
    }

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

    /**
     * 新增收藏
     * @param matchId 比赛ID
     */
    suspend fun addCollect(matchId: Long) = withContext(scope.coroutineContext) {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.AddCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.ADD_COLLECT
        ) {
            Client.AddCollectReq.newBuilder().apply {
                addMatchId(matchId)
            }.build()
        }

        if (result.error == null && result.data != null) {
            ApiResponseState.Succeeded(result.data)
        } else {
            ApiResponseState.Failed(error = result.error)
        }
    }

    /**
     * 删除收藏
     * @param matchId 比赛ID
     */
    suspend fun removeCollect(matchId: Long) = withContext(scope.coroutineContext) {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.RemoveCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.REMOVE_COLLECT
        ) {
            Client.RemoveCollectReq.newBuilder().apply {
                addMatchId(matchId)
            }.build()
        }

        if (result.error == null && result.data != null) {
            ApiResponseState.Succeeded(result.data)
        } else {
            ApiResponseState.Failed(error = result.error)
        }
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
    ) = withContext(scope.coroutineContext) {
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

        if (result.error == null && result.data != null) {
            ApiResponseState.Succeeded(result.data?.toSearchResultBean())
        } else {
            ApiResponseState.Failed(error = result.error)
        }
    }

    /**
     * 获取搜索推荐词
     * @param keyword 搜索关键词
     */
    suspend fun getSearchRecommend(keyword: String? = "") = withContext(scope.coroutineContext) {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.SearchRecommendResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SEARCH_RECOMMEND,
        ) {
            Client.SearchRecommendReq.newBuilder().apply {
                word = keyword
            }.build()
        }

        if (result.error == null && result.data != null) {
            ApiResponseState.Succeeded(result.data?.recommendList?.toList())
        } else {
            ApiResponseState.Failed(error = result.error)
        }
    }

    /**
     * 获取搜索热词
     * @return 返回热词列表
     */
    suspend fun getSearchHotWord() = withContext(scope.coroutineContext) {
        val result = socketManager.sendAndWaitProtoMessageResponse<Client.SearchHotWordResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SEARCH_HOT_WORD,
        ) {
            Client.SearchHotWordReq.newBuilder().build()
        }

        if (result.error == null && result.data != null) {
            ApiResponseState.Succeeded(result.data?.wordList?.toList())
        } else {
            ApiResponseState.Failed(error = result.error)
        }
    }

    // ================== 遊戲供應商相關方法 ==================

    /**
     * 從本地 Room 查詢指定遊戲分類下的供應商列表。
     * 用於「遊戲廠商精準響應詞」判斷和 Tab 列表展示。
     */
    suspend fun querySuppliersByGameType(gameTypeId: Int): List<GameSupplierDataModel> {
        return withContext(Dispatchers.IO) {
            database.supplierDao().querySupplier(gameTypeId)
        }
    }

    /**
     * 根據關鍵字查找供應商（精準匹配）。
     * 用於判斷是否為「遊戲廠商精準響應詞」。
     */
    suspend fun findSupplierByKeyword(
        keyword: String,
        gameTypeId: Int = 0
    ): GameSupplierDataModel? {
        if (keyword.isBlank()) return null
        val suppliers = querySuppliersByGameType(gameTypeId)
        val lower = keyword.lowercase()
        return suppliers.firstOrNull { supplier ->
            val name = supplier.name.lowercase()
            name == lower || name.startsWith(lower)
        }
    }

    /**
     * 根據 ID 查詢供應商。
     */
    suspend fun querySupplierById(id: Int): GameSupplierDataModel? {
        return withContext(Dispatchers.IO) {
            database.supplierDao().querySupplierById(id)
        }
    }

    /**
     * 根據關鍵字匹配遊戲分類（例如：電子、老虎機）。
     * 返回對應的 gameTypeId，如果沒有匹配則返回 null。
     */
    suspend fun matchGameCategoryByKeyword(keyword: String): Int? {
        val lower = keyword.lowercase()
        // 常見遊戲分類關鍵字映射
        val categoryMap = mapOf(
            "電子" to 4,
            "老虎機" to 4,
            "slot" to 4,
            "slots" to 4,
            "瓦力遊戲" to 0, // 全部
            "瓦力" to 0,
        )
        return categoryMap[lower]
    }

    /**
     * 監聽指定遊戲分類下的供應商數據變化（用於 BottomSheet）。
     */
    suspend fun observeSupplierByGameTypeId(gameTypeId: Int) = withContext(Dispatchers.IO) {
        database.supplierDao().observeSupplierGameTypeId(gameTypeId)
    }

    /**
     * 設置供應商的選中狀態（用於 BottomSheet 確認後保存選中狀態）。
     */
    suspend fun setSupplierSelectIds(gameTypeId: Int, ids: List<Int>) {
        withContext(Dispatchers.IO) {
            val selectedIdSet = ids.toSet()
            val suppliers = querySuppliersByGameType(gameTypeId)
            suppliers.forEach { supplier ->
                supplier.isSelected = if (supplier.id in selectedIdSet) 1 else 0
            }
            database.supplierDao().insert(suppliers)
        }
    }
}