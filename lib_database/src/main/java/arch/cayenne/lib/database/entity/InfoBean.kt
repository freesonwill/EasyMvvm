package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InfoBean(
    @PrimaryKey val uid: Int,
    val balance: Long,
    val currency: String,
    var login: Boolean
)
