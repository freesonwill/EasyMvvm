package com.walisport.module.search.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import org.koin.java.KoinJavaComponent.inject

class SearchRepository(override val scope: CoroutineScope) : BaseRepository() {

    private val manager: UserDataManager by inject(UserDataManager::class.java)

    //删除某搜索关键字
    fun deleteOneRecord(keyword: String?) {
        val record = manager.getValue(UserDataKey.KEY_RECORD, "")
        if (record.isNotEmpty()) {
            val uid = manager.getValue(UserDataKey.KEY_UID, -1)
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
            manager.setKeyValue(UserDataKey.KEY_RECORD, json)
        }
    }

    //增加一条搜索关键字
    fun addOneRecord(key: String) {
        val record = manager.getValue(UserDataKey.KEY_RECORD, "")
        val uid = manager.getValue(UserDataKey.KEY_UID, -1)
        if (record.isEmpty()) {//为空直接添加
            val temp = RecordBean(uid, key)
            val list = listOf(temp)
            val json = Gson().toJson(list)
            manager.setKeyValue(UserDataKey.KEY_RECORD, json)
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
                manager.setKeyValue(UserDataKey.KEY_RECORD, json)
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
        val record = manager.getValue(UserDataKey.KEY_RECORD, "")
        if (record.isNotEmpty()) {
            val uid = manager.getValue(UserDataKey.KEY_UID, -1)
            val list = getRecordList(record)
            for (item in list) {
                val iid = item.uid
                if (iid == uid) {
                    item.record = ""
                }
            }
            val json = Gson().toJson(list)
            manager.setKeyValue(UserDataKey.KEY_RECORD, json)
        }
    }

    //根据本账号uid来匹配搜索记录
    fun getRecordByUID(): List<String> {
        val historyList: MutableList<String> = ArrayList()
        val record = manager.getValue(UserDataKey.KEY_RECORD, "")
        if (record.isNotEmpty()) {
            val uid = manager.getValue(UserDataKey.KEY_UID, -1)
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
}