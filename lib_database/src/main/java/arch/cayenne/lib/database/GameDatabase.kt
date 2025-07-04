package arch.cayenne.lib.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.BetSlipOrderDao
import arch.cayenne.lib.database.dao.BetSlipReserveDao
import arch.cayenne.lib.database.dao.InfoDao
import arch.cayenne.lib.database.dao.LiveMatchDao
import arch.cayenne.lib.database.dao.LiveVideoDao
import arch.cayenne.lib.database.dao.MarketTypeBeanDao
import arch.cayenne.lib.database.dao.MarketTypeMenuBeanDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.dao.MessageDao
import arch.cayenne.lib.database.dao.SportDao
import arch.cayenne.lib.database.dao.TournamentDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.LiveMarketBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MarketTypeBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.MessageBean
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionsEdit
import arch.cayenne.lib.database.entity.SportBean
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.lib.database.entity.TournamentBean
import arch.cayenne.lib.database.entity.TournamentMatchRef

@Database(
    entities = [
        InfoBean::class,
        MessageBean::class,
        BetBean::class,
        BetSelectionBean::class,
        BetDetailBean::class,
        LiveVideoBean::class,
        SportBean::class,
        SportTournamentCrossRef::class,
        TournamentBean::class,
        TournamentMatchRef::class,
        MatchBean::class,
        MarketBean::class,
        SelectionBean::class,
        MatchMarketCrossRef::class,
        MarketSelectCrossRef::class,
        MarketTypeBean::class,
        MarketMenuBean::class,
        LiveMatchBean::class,
        LiveMarketBean::class,
        LiveSelectionBean::class,
        LiveSelectionBeanRecord::class,
        BetSlipOrderBean::class,
        BetSlipReserveBean::class,
        SelectionsEdit::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(VideoSourceBeanConverter::class, MarketTypeBeanConverter::class, BetSlipTypeConverter::class)
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
    abstract fun tournamentDao(): TournamentDao
    abstract fun liveVideoDao(): LiveVideoDao
    abstract fun marketTypeDao(): MarketTypeBeanDao
    abstract fun marketTypeMenuDao(): MarketTypeMenuBeanDao
    abstract fun matchDao(): MatchDao
    abstract fun liveMatchDao(): LiveMatchDao
    abstract fun msgDao(): MessageDao
    abstract fun infoDao(): InfoDao
    abstract fun betSlipOrderDao(): BetSlipOrderDao
    abstract fun betSlipReserveDao(): BetSlipReserveDao
}