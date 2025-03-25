package com.walisport.lib_databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [],
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
            Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java).build()

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            GameDatabase::class.java,
            "game_database.db"
        ).build()
    }
}