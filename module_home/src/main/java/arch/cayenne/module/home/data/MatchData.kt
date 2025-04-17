package arch.cayenne.module.home.data



//TODO 暫時放置的資料結構，待更新
data class Match(
    val matchId: Long,
    val collect: Boolean,
    val basicInfo: MatchBasicInfo,
    val market: List<Market>,
)

data class MatchBasicInfo(
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
    val liveInfo: MatchLiveInfo,
    val betStop: Boolean,// false: 未停止投注, true: 已停止投注
    val tournamentHot: Boolean,
    val tournamentWeight: Int,
)
data class MatchLiveInfo(
    val clock: Int,//走表时间，以秒为单位
    val rollClock: Boolean,//是否走表
    val period: String,//阶段
    val score: String,//比分
    val liveVideo: Boolean,//该比赛是否有视频或者直播
    val charRoom: Boolean,//是否开启了聊天室
    val viewerCount: Int,//观看数量
    val clockModified: Long,//走表修改时间
)
data class Market(
    val marketId: Long,
    val marketName: String,
    val marketDetail: List<MarketDetail>,
    val status: Int,//0-默认 1-新增 2-修改 3-删除 (仅在推送时使用)
)
data class MarketDetail(
    val specifier: String,//盘口说明符，可能为空
    val selection: List<Selection>,
    val active: Boolean,//true - 可以投注  false - 不可投注
    val parlay: Boolean,//true - 支持串关  false - 不支持串关
)
data class Selection(
    val selectionId: Long,
    val name: String,
    val shortName: String,
    val odds: String,
    val active: Boolean,
    val parlay: Boolean,
)