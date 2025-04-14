package com.walisport.lib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.walisport.lib.database.dao.BetDao
import com.walisport.lib.database.dao.SportCategoryDao
import com.walisport.lib.database.dao.SportDao
import com.walisport.lib.database.dao.TournamentCategoryDao
import com.walisport.lib.database.dao.TournamentDao
import com.walisport.lib.database.entity.BetBean
import com.walisport.lib.database.entity.SportBean
import com.walisport.lib.database.entity.SportCategory
import com.walisport.lib.database.entity.TournamentBean
import com.walisport.lib.database.entity.TournamentCategory

@Database(
    entities = [BetBean::class, SportBean::class, SportCategory::class, TournamentBean::class, TournamentCategory::class],
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

    abstract fun sportDao(): SportDao

    abstract fun sportCategoryDao(): SportCategoryDao

    abstract fun tournamentDao(): TournamentDao

    abstract fun tournamentCategoryDao(): TournamentCategoryDao
}