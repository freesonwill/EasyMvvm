package com.walisport.module.business.common.data

import arch.cayenne.lib.http.data.AvatarVo
import arch.cayenne.lib.http.data.PaginationVo
import com.walisport.module.business.common.data.constants.GameSortType

/**
 *
 * @date: 2025/12/26 01:26
 * @description:
 */

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
    val pagination: PaginationVo, // 分页信息
    val list: List<ProfilePlayedVo> // 分页数据
)

fun ProfilePlayedVo.toGameContentData(): GameContentData {
    return GameContentData(
        id = this.gameType.toLong() ,
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

fun GameVo.toGameContentData(sortType: GameSortType? = null): GameContentData {
    return GameContentData(
        id = this.id.toLong(),
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
 *  游戏收藏/取消收藏请求参数
 *  @date: 2025/12/24
 */
data class ProfileCollectEditVo(
    val gameType: Int , // 游戏类型
    val collect: Boolean? // 是否收藏，true:收藏，false:取消收藏，不传则自动转换
)