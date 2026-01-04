package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SystemAvatarBean(
    @PrimaryKey val id: Int,
    val url: String,
    val host: String
)
