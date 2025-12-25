package com.walisport.module.hall.data

import arch.cayenne.lib.http.data.AvatarVo
import arch.cayenne.lib.http.data.PaginationVo
import com.walisport.module.hall.data.constants.GameSortType


/**
 *
 * @date: 2025/12/18 21:58
 * @description:
 */
data class BigVo(
    val id: Long , // 游戏ID
    val name: String , // 玩家名称
    val avatar: AvatarVo , // 图片信息
    val ccy: String , // 货币缩写(ISO4217)
    val multiple: Int , // 投注倍率
    val bonus: Int // 输赢金额
)



/**
 *
 * @date: 2025/12/18 22:01
 * @description:
 */
data class BigPageVo(val pagination: PaginationVo , val list: List<BigVo>)

/**
 *分页数据
 * @date: 2025/12/11 11:43
 * @description:
 */
data class GameVo(
    val id: Int ,//游戏ID
    val name: String ,//游戏名称
    val avatar: AvatarVo ,//图片信息
    val online: Int ,// 当前在线人数
    val reward: Float ,//返奖率
    val hasMore: Boolean//是否有更多数据
)

fun GameVo.toGameContentData(id: Long, sortType: GameSortType? = null): GameContentData {
    return GameContentData(
        id = id,
        name = this.name ,
        avatar = Avatar(
            url = this.avatar.url ,
            thumbhash = this.avatar.thumbhash ,
            css = ""
        ) ,
        online = this.online ,
        reward = this.reward.toDouble() ,
        hasMore = this.hasMore ,
        hotOrCold = when (sortType) {
            GameSortType.HOT_REWARD -> {
                HotColdType.HOT
            }

            GameSortType.COLD_REWARD -> {
                HotColdType.COLD
            }

            else -> {
                HotColdType.NONE
            }
        }
    )
}

/**
 *
 * @date: 2025/12/11 11:38
 * @description:
 */
data class GamePageVo(val pagination: PaginationVo , val list: List<GameVo>) {
}

/**
 *
 * @date: 2025/12/18 17:32
 * @description:
 */
data class BettingVo(
    val id: Long ,//游戏ID
    val name: String ,//玩家名称
    val avatar: AvatarVo ,//图片信息
    val ccy: String ,//example: USD 货币缩写(ISO4217)
    val multiple: Int , //投注倍率
    val bonus: Int//输赢金额
)



/**
 *
 * @date: 2025/12/18 17:36
 * @description:
 */
data class BettingPageVo(val pagination: PaginationVo , val list: List<BettingVo>)

/**
 * 每日投注比赛信息
 *
 * @property ccy 货币缩写(ISO4217)
 * @property betScore 投注奖金金额
 * @property remainingTime 活动剩余时长/s
 */
data class DailyBetMatchVo(
    val ccy: String , // 货币缩写(ISO4217)
    val betScore: Long , // 投注奖金金额
    val remainingTime: Long // 活动剩余时长/s
)

data class DailyBetMatchData(
    val ccy: String , // 货币缩写(ISO4217)
    val betScore: Long , // 投注奖金金额
    val remainingTime: Long // 活动剩余时长/s
)

fun DailyBetMatchVo.toDailyBetMatchData(): DailyBetMatchData {
    return DailyBetMatchData(
        ccy = ccy ,
        betScore = betScore ,
        remainingTime = remainingTime
    )
}

/**
 * 分页数据
 * @date: 2025/12/19
 * @description:
 */
data class DayVo(
    val ranking: Int , // 排名
    val uid: Long , // 玩家UID
    val name: String , // 玩家名称
    val ccy: String , // 货币缩写(ISO4217)
    val bet: Long , // 投注金额
    val bonus: Int , // 奖金金额
    val mySelf: Boolean // 是否自己
)

data class DayPageVo(val pagination: PaginationVo , val list: List<DayVo>)

fun List<GameAllRankingToday>.addDashItem(): List<GameAllRankingToday> {
    //添加分割线
    //遍历列表， 如果某个item的rank和下一个item的rank不连续，则在它们之间添加一个DashItem， 只添加一次
    val newList = mutableListOf<GameAllRankingToday>()
    var dashAdded = false
    for (i in indices) {
        newList.add(this[i])
        if (!dashAdded && i < this.size - 1) {
            val currentRank = when (val item = this[i]) {
                is GameAllRankingToday.GameAllRankingTodayData -> item.rank
                else -> null
            }
            val nextRank = when (val item = this[i + 1]) {
                is GameAllRankingToday.GameAllRankingTodayData -> item.rank
                else -> null
            }
            if (currentRank != null && nextRank != null && nextRank - currentRank > 1) {
                newList.add(GameAllRankingToday.GameAllRankingDashData)
                dashAdded = true
            }
        }
    }

    return newList
}

/**
 * 游戏详情
 * @date: 2025/12/20
 * @description:
 */
data class GameDetailVo(
    val gameType: Int, // 游戏ID
    val name: String, // 游戏名称
    val avatar: List<AvatarVo>, // 游戏缩略图
    val online: Int, // 在线人数
    val reward: Float, // 返奖率
    val hasMore: Boolean, // 更多数据
    val collect: Boolean, // 是否被收藏
    val score: Double, // 评分
    val comments: Int, // 评论人数
    val maxOdds: Int, // 最高返奖赔率
    val category: Int, // 游戏分类
    val supplier: String, // 供应商名称
    val tryIt: Boolean, // 是否支持试玩
    val tryItUrl: String, // 试玩地址
    val gameUrl: String, // 游戏地址
    val materials: String, // 游戏介绍物料
    val ccyList: List<String>, // 支持货币
    val videoUrl: String, // 游戏视频播放链接
    val icon: String // 游戏图标
)

/**
 * 分页数据
 */
data class ProfilePlayedVo(
    val gameType: Int, // 游戏ID
    val name: String, // 游戏名称
    val avatar: AvatarVo, // 图片信息
    val online: Int, // 在线人数
    val reward: Double // 返奖率
)


/**
 * 游戏收藏列表分页数据
 */
data class ProfilePlayedPageVo(
    val pagination: PaginationVo , // 分页信息
    val list: List<ProfilePlayedVo> // 分页数据
)

fun ProfilePlayedVo.toGameContentData(id: Long): GameContentData {
    return GameContentData(
        id = id,
        name = this.name ,
        avatar = Avatar(
            url = this.avatar.url ,
            thumbhash = this.avatar.thumbhash ,
            css = ""
        ) ,
        online = this.online ,
        reward = this.reward ,
        hasMore = false ,
        hotOrCold = HotColdType.NONE
    )
}
