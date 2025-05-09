package arch.cayenne.lib.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* Basic Bean
* */

@Entity
data class LiveMatchBean(
    @PrimaryKey val matchId: Long,
    val collect: Boolean,
    @Embedded(prefix = "basic_") val basicInfo: LiveMatchBasicInfoBean,
    @Embedded(prefix = "live_") val liveInfo: LiveMatchLiveInfoBean,
)

@Entity
data class LiveMarketBean(
    @PrimaryKey val marketId: Long,
    val marketName: String,
    val status: Int
)

@Entity
data class LiveSelectionBean(
    @PrimaryKey val selectionId: Long,
    @Embedded(prefix = "detail_") val detail: LiveMarketDetailBean,
    val name: String,
    val shortName: String,
    val odds: Int,
    val active: Boolean,
    val parlay: Boolean,
    val marketId: Long,
    val style :Int//0-默认 1-一列 2-两列 3-三列 4-波胆
)

/*
* Embedded Class
* */

data class LiveMatchBasicInfoBean(
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

data class LiveMatchLiveInfoBean(
    val clock: Int,//走表时间，以秒为单位
    val rollClock: Boolean,//是否走表
    val period: String,//阶段
    val score: String,//比分
    val liveVideo: Boolean,//该比赛是否有视频或者直播
    val charRoom: Boolean,//是否开启了聊天室
    val viewerCount: Int,//观看数量
    val clockModified: Long,//走表修改时间
)

data class LiveMarketDetailBean(
    val detailId: Int,
    val specifier: String?,
    val active: Boolean,
    val parlay: Boolean
)





