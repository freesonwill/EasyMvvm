package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

//記錄每個PlayType現在是點在哪個選項上
@Entity
data class HomeSelectedBean(
    @PrimaryKey val playType: Int,
    val sportId: Int,
    val tournamentIdList: List<Int>,
)