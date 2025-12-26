package com.walisport.module.business.common.data

import arch.cayenne.lib.http.data.AvatarVo
import arch.cayenne.lib.http.data.PaginationVo

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