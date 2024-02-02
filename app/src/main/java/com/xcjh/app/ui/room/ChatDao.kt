package com.xcjh.app.ui.room

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.alibaba.fastjson.JSONObject
import com.xcjh.app.bean.MsgBeanData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.utils.LogUtils

@Dao
interface ChatDao {

    //    @Query("select * from chat_db order by [id] desc  limit  :start,:count ")
    @Query("SELECT * FROM chat_db WHERE anchorId = :anchorId AND  withId = :id ORDER BY createTime DESC")
    fun getMessagesByName(anchorId: String, id: String): MutableList<MsgBeanData>

    @Query("SELECT * FROM chat_db WHERE sendId = :sendId LIMIT 1")
    fun findMessagesById(sendId: String): MsgBeanData

    @Transaction
    suspend fun insertOrUpdate(message: MsgBeanData) {

            var oldMessage: MsgBeanData?
            oldMessage = findMessagesById(message.sendId!!)
            Log.d("MessageDao", "oldMessage: $oldMessage")

            if (oldMessage != null) {

                message.idd = oldMessage!!.idd
                updateData(message)

            } else {
                insert(message)

        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MsgBeanData)

    @Update
    fun updateData(entity: MsgBeanData?)

    @Delete
    fun delete(message: MsgBeanData)

    @Query("DELETE FROM chat_db WHERE anchorId = :anchorId")
    fun deleteAllZeroId(anchorId: String)
}