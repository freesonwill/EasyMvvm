package arch.cayenne.lib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import arch.cayenne.lib.database.dao.SportCategoryDao
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.lib.database.dao.TournamentCategoryDao
import arch.cayenne.lib.database.dao.TournamentDao
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportCategory
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentCategory
import androidx.room.TypeConverters
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.LiveVideoDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.SelectionBean

@Database(
    entities = [
        InfoBean::class,
        BetBean::class,
        LiveVideoBean::class,
        SportBean::class,
        SportCategory::class,
        TournamentBean::class,
        TournamentCategory::class,
        MatchBean::class,
        MarketBean::class,
        SelectionBean::class,
        MatchMarketCrossRef::class,
        MarketSelectCrossRef::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(GameTypeConverter::class, VideoSourceBeanConverter::class)
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

    abstract fun liveVideoDao(): LiveVideoDao

    abstract fun matchDao(): MatchDao

    abstract fun infoDao(): InfoDao
}