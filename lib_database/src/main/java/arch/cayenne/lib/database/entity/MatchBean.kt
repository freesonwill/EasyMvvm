package arch.cayenne.lib.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/*
* Basic Bean
* */

@Entity
data class MatchBean(
    @PrimaryKey val matchId: Long,
    val collect: Boolean,
    @Embedded(prefix = "basic_") val basicInfo: MatchBasicInfoBean,
    @Embedded(prefix = "live_") val liveInfo: MatchLiveInfoBean,
) {
//    override fun equals(newItem: Any?): Boolean {
//        return newItem is MatchBean &&
//                matchId == newItem.matchId &&
//                basicInfo.status == newItem.basicInfo.status &&
//                basicInfo.betStop == newItem.basicInfo.betStop &&
//                basicInfo.startTime == newItem.basicInfo.startTime
//    }
}

@Entity
data class MarketBean(
    @PrimaryKey val marketId: Long,
    val marketName: String,
    val status: Int
)

@Entity
data class SelectionBean(
    @PrimaryKey val selectionId: Long,
    @Embedded(prefix = "detail_") val detail: MarketDetailBean,
    val name: String,
    val shortName: String?,
    val odds: Int,
    val active: Boolean,
    val parlay: Boolean
)

/*
* Cross Reference Entity
* */

@Entity(primaryKeys = ["playType", "tournamentId", "matchId", "startTime", "page"])
data class TournamentMatchRef(
    val playType: Int,
    val tournamentId: Int,
    val page: Int,
    val startTime: Long, //0表示取得ALL
    val matchId: Long,
    val order: Int,
)

@Entity(primaryKeys = ["matchId", "marketId"],)
data class MatchMarketCrossRef(
    val matchId: Long,
    val marketId: Long
)

@Entity(primaryKeys = ["matchId", "marketId", "selectionId"],)
data class MarketSelectCrossRef(
    val matchId: Long,
    val marketId: Long,
    val selectionId: Long,
)

/*
* Embedded Class
* */

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

data class MarketDetailBean(
    val detailId: Int,
    val specifier: String?,
    val active: Boolean,
    val parlay: Boolean
)

data class MarketWithSelections(
    val market: MarketBean,
    val selections: List<SelectionBeanLite>
) {
//    override fun equals(newItem: Any?): Boolean {
//        return newItem is MarketWithSelections &&
//                market == newItem.market
//
//    }
}

data class SelectionBeanLite(
    val selectionId: Long,
    val detailActive: Boolean,
    val name: String,
    val shortName: String?,
    val odds: Int,
    val active: Boolean,
    val parlay: Boolean,
    var isSelected: Boolean = false,
    var trend: Int = 0,
)

data class MatchWithMarkets(
    val match: MatchBean,
    val markets: List<MarketWithSelections>
) {
//    override fun equals(newItem: Any?): Boolean {
//        return newItem is MatchWithMarkets &&
//                match == newItem.match &&
//                markets == newItem.markets
//    }
}

//用來做notify收到時組合起來更新資料表用的
data class MatchBeanLite(
    val matchId: Long,
    val status: Int,  //update MatchBasicInfoBean
    val betStop: Boolean, //update MatchBasicInfoBean
    val startTime: Long, //update MatchBasicInfoBean
    val liveInfo: MatchLiveInfoBean  //update MatchLiveInfoBean
)
