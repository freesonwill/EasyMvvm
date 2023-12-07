package com.xcjh.app.ui.room


import androidx.room.Database
import androidx.room.RoomDatabase

/***
 * 聊天列表数据room
 */
@Database(entities = [MsgListNewData::class], version = 1,exportSchema = false)
abstract class MyRoomChatList : RoomDatabase() {
    abstract val chatDao: ChatListDao?
}