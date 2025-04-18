package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LiveVideoBean")
data class LiveVideoBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val name: String = "",
    val urlSource: String = "",
    val streamType: String = "",
    val rtmpUrl: String = "",
    val m3U8Url: String = "",
    val flvUrl: String = "",
    val language: String = "",

    val sources: String = "",
    val thumb: String = "",
    val title: String = "",
    val subTitle: String = "",
    var isPlaying: Boolean = false,

    ) {

    fun playUrl(): String {
        return rtmpUrl.takeIf { it.isNotEmpty() }
            ?: m3U8Url.takeIf { it.isNotEmpty() }
            ?: flvUrl.takeIf { it.isNotEmpty() }
            ?: ""
    }
}
