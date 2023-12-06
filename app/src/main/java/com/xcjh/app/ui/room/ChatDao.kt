package com.xcjh.app.ui.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChatDao {
    //    @Query("select * from chat_db order by [id] desc  limit  :start,:count ")
    @Query("SELECT * FROM chat_db WHERE anchorId = :anchorId ORDER BY createTime DESC")
    fun getMessagesByName(anchorId: String): MutableList<MsgBeanData >

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MsgBeanData)
    @Update
    fun updateData(entity: MsgBeanData?)
    @Delete
    fun delete(message: MsgBeanData)
    @Query("DELETE FROM chat_db WHERE anchorId = :anchorId")
    fun deleteAllZeroId(anchorId:String)
}