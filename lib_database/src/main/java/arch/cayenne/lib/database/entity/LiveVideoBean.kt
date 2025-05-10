package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LiveVideoBean")
data class LiveVideoBean(
    @PrimaryKey
    val matchId: Long,//比赛ID
    val source: List<VideoSourceBean>, //视频源列表
)

data class VideoSourceBean(
    val id: Int = 0,
    val name: String = "",
    val urlSource: String = "",
    val streamType: String = "",
    val rtmpUrl: String = "",
    val m3U8Url: String = "",
    val flvUrl: String = "",
    val language: String = "",

    val thumb: String = "",
    val title: String = "",
    val subTitle: String = "",
    var isPlaying: Boolean = false,
) {

    fun playUrl(): String {
        return flvUrl.takeIf { it.isNotEmpty() }
            ?: m3U8Url.takeIf { it.isNotEmpty() }
            ?: rtmpUrl.takeIf { it.isNotEmpty() }
            ?: ""
    }

}

