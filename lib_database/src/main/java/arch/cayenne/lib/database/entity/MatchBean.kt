package arch.cayenne.lib.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "MatchBean")
data class MatchBean(
    @PrimaryKey val matchId: Long,
    val collect: Boolean,
    @Embedded(prefix = "basic_") val basicInfo: MatchBasicInfoBean,
    @Embedded(prefix = "live_") val liveInfo: MatchLiveInfoBean,
)

@Entity(
    tableName = "MarketBean",
    foreignKeys = [
        ForeignKey(
            entity = MatchBean::class,
            parentColumns = ["matchId"],
            childColumns  = ["matchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("matchId")]
)
data class MarketBean(
    @PrimaryKey val marketId: Long,
    val matchId: Long,
    val marketName: String,
    val status: Int
)

@Entity(
    tableName = "MarketDetailBean",
    foreignKeys = [
        ForeignKey(
            entity = MarketBean::class,
            parentColumns = ["marketId"],
            childColumns  = ["marketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("marketId")]
)
data class MarketDetailBean(
    @PrimaryKey val detailId: Long,
    val marketId: Long,
    val specifier: String?,
    val active: Boolean,
    val parlay: Boolean
)

@Entity(
    tableName = "SelectionBean",
    foreignKeys = [
        ForeignKey(
            entity = MarketDetailBean::class,
            parentColumns = ["detailId"],
            childColumns  = ["detailId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("detailId")]
)
data class SelectionBean(
    @PrimaryKey val selectionId: Long,
    val detailId: Long,
    val name: String,
    val shortName: String?,
    val odds: String,
    val active: Boolean,
    val parlay: Boolean
)

// 巢狀關聯：Match -> Markets -> MarketDetail -> Selection
data class MarketDetailWithSelections(
    @Embedded val detail: MarketDetailBean,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val selections: List<SelectionBean>
)

data class MarketWithMarketDetails(
    @Embedded val market: MarketBean,
    @Relation(
        parentColumn = "marketId",
        entityColumn = "marketId"
    )
    val details: List<MarketDetailBean>
)

data class MatchWithMarkets(
    @Embedded val match: MatchBean,
    @Relation(
        parentColumn = "matchId",
        entityColumn = "matchId"
    )
    val markets: List<MarketBean>
)



data class MatchBasicInfoBean(
    val matchId: Long,
    val matchName: String,
    val homeTeam: String,
    val homeTeamId: Int,
    val homeTeamIcon: String,
    val awayTeam: String,
    val awayTeamId: Int,
    val awayTeamIcon: String,
    val startTime: Long,
    val status: Int, //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
    val tournamentId: Int,
    val tournamentName: String,
    val tournamentShortName: String,
    val tournamentIcon: String,
    val sportId: Int,
    val sportName: String,
    val betStop: Boolean,// false: 未停止投注, true: 已停止投注
    val tournamentHot: Boolean,
    val tournamentWeight: Int,
)

data class MatchLiveInfoBean(
    val clock: Int,//走表时间，以秒为单位
    val rollClock: Boolean,//是否走表
    val period: String,//阶段
    val score: String,//比分
    val liveVideo: Boolean,//该比赛是否有视频或者直播
    val charRoom: Boolean,//是否开启了聊天室
    val viewerCount: Int,//观看数量
    val clockModified: Long,//走表修改时间
)
