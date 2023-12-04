package com.xcjh.app.ui.room


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MsgBeanData::class], version = 1)
abstract class MyRoomDataBase : RoomDatabase() {
    abstract val chatDao: ChatDao?
}