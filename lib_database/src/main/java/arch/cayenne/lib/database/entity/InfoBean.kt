package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InfoBean(
    @PrimaryKey val uid: Int,
    val balance: Long,
    var login: Boolean
)
