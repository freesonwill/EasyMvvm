package arch.cayenne.lib.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["sportId"])
data class SportBean(
    val sportId: Int,
    val sportName: String,
    val matchCount: Int,
    val sportOrder: Int
)

//@Entity(primaryKeys = ["playType","sportId"])
//data class PlayTypeSportCrossRef(
//    val playType: Int,
//    val sportId: Int,
//    val matchCount: Int,
//    val sportOrder: Int
//)

data class SportDataModel (
    val id: Int,
    val matchCount: Int,
    val order: Int,
)

