package com.walisport.lib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.walisport.lib.database.dao.BetDao
import com.walisport.lib.database.entity.BetBean

@Database(
    entities = [BetBean::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase: RoomDatabase() {

    companion object {
        @Volatile
        private var instance: GameDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        fun invokeTestDatabase(context: Context) =
            Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()

        private fun buildDatabase(context: Context) =
            Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
                .build()
    }

    abstract fun betDao(): BetDao
}