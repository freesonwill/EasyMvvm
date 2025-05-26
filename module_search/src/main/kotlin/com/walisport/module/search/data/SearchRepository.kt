package com.walisport.module.search.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class SearchRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
) : BaseRepository() {

    //删除某搜索关键字
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

    //增加一条搜索关键字
    fun addOneRecord(key: String) {
        val record = userDataManager.getValue(UserDataKey.KEY_RECORD, "")
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        if (record.isEmpty()) {//为空直接添加
            val temp = RecordBean(uid, key)
            val list = listOf(temp)
            val json = Gson().toJson(list)
            userDataManager.setKeyValue(UserDataKey.KEY_RECORD, json)
        } else {//记录不为空有2种情况：1有数据但无本账户数据 2有数据且存在本账户的数据
            val list = getRecordList(record)
            var isExist = false
            for (item in list) {
                val iid = item.uid
                if (iid == uid) {
                    val keyword = item.record
                    isExist = true
                    if (!keyword.contains(key)) {//不包含关键字才添加
                        item.record = item.record + ";" + key
                    }
                }
            }
            if (isExist) {//有本账户数据则直接更新
                val json = Gson().toJson(list)
                userDataManager.setKeyValue(UserDataKey.KEY_RECORD, json)
            } else {//无本账户数据则添加
                val history: MutableList<RecordBean> = ArrayList()
                val tmp = RecordBean(uid, key)
                history.addAll(list)
                history.add(tmp)
            }
        }
    }

    //删除本账户对应的所有搜索记录
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

    //根据本账号uid来匹配搜索记录
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

    //JSON格式 {"uid": 43213,"record":"衣服;男鞋;香蕉;苹果;红薯"}
    private fun getRecordList(value: String): List<RecordBean> {
        val type = object : TypeToken<List<RecordBean>>() {}.type
        return Gson().fromJson(value, type)
    }

    suspend fun getSearchResult(key: String) {
        //TODO
        val response = socketManager.sendAndWaitProtoMessageResponse<Client.SearchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.SEARCH,
        ) {
            Client.SearchReq.newBuilder().apply {
                word = key
//                val word: string = 1 //required 搜索词
//                val type: int32 = 2 //required 1-普通词  2-热门词  10-球员id  11-球队id  12-联赛id
//                val start_time: int64 = 3 //开始时间
//                val end_time: int64 = 4 //结束时间
//                val page: int32 = 5 //请求的页数,从1开始
//                val size: int32 = 6 //每页显示数量,最大50
//                val time_zone: int32 = 7 //时区，例如上海时间+8就传8, utc时间传0
            }.build()
        }
        println(response)
    }

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