package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SportLoginInfoBean(
    @PrimaryKey val index: Int,
    val isLogin: Boolean
)
