package com.xcjh.app.ui.room

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.xcjh.app.bean.MsgListNewData

/**
 * 聊天列表数据
 */
@Dao
interface ChatListDao {
    @Query("SELECT * FROM chat_listdb WHERE withId = :id ORDER BY createTime DESC")
    fun getAll(id:String): List<MsgListNewData>

    @Query("SELECT * FROM chat_listdb WHERE anchorId = :id LIMIT 1")
    fun findMessagesById(id: String): MsgListNewData
    @Transaction
    fun insertOrUpdate(message: MsgListNewData) {

        val oldMessage = findMessagesById(message.anchorId!!)
        Log.d("MessageDao", "oldMessage: $oldMessage")

        if (oldMessage != null) {

            message.idd=oldMessage.idd
            update(message)


        } else {

            insertAll(message)
        }
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( messages: MsgListNewData)
    @Update
    fun update(message: MsgListNewData)
    @Delete
    fun delete(message: MsgListNewData)


}