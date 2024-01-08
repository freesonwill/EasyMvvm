package com.xcjh.app.ui.room


import androidx.room.Database
import androidx.room.RoomDatabase
import com.xcjh.app.bean.MsgBeanData

/***
 * 私聊room
 */
@Database(entities = [MsgBeanData::class], version = 1,exportSchema = false)
abstract class MyRoomDataBase : RoomDatabase() {
    abstract val chatDao: ChatDao?
}