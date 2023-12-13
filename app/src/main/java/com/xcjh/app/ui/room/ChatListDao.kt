package com.xcjh.app.ui.room

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ChatListDao {
    @Query("SELECT * FROM chat_listdb")
    fun getAll(): List<MsgListNewData>

    @Query("SELECT * FROM chat_listdb WHERE anchorId = :id LIMIT 1")
    fun findMessagesById(id: String): MsgListNewData
    @Transaction
    fun insertOrUpdate(message: MsgListNewData) {
        val data = getAll()
        val oldMessage = findMessagesById(message.anchorId!!)
        Log.d("MessageDao", "oldMessage: $oldMessage")

        if (oldMessage != null) {

            message.idd=oldMessage.idd
            update(message)
            val data = getAll()
            Log.d("MessageDao", "Updating message...")
        } else {
            Log.d("MessageDao", "Inserting new message...")
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