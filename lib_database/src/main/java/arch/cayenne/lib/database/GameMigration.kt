package arch.cayenne.lib.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object GameMigration {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {

        }
    }
}