package com.walisport.module.business.common.data.model

import arch.cayenne.lib.http.data.AvatarVo
import arch.cayenne.lib.http.data.PaginationVo

/**
 *
 * @date: 2025/12/24 20:59
 * @description:
 */

/**
 * 游戏详情
 * @date: 2025/12/20
 * @description:
 */
data class GameDetailVo(
    val gameType: Int , // 游戏ID
    val name: String , // 游戏名称
    val avatar: List<AvatarVo> , // 游戏缩略图
    val online: Int , // 在线人数
    val reward: Float , // 返奖率
    val hasMore: Boolean , // 更多数据
    val collect: Boolean , // 是否被收藏
    val score: Double , // 评分
    val comments: Int , // 评论人数
    val maxOdds: Int , // 最高返奖赔率
    val category: Int , // 游戏分类
    val supplier: String , // 供应商名称
    val tryIt: Boolean , // 是否支持试玩
    val tryItUrl: String , // 试玩地址
    val gameUrl: String , // 游戏地址
    val materials: String , // 游戏介绍物料
    val ccyList: List<String> , // 支持货币
    val videoUrl: String , // 游戏视频播放链接
    val icon: String // 游戏图标
)

/**
 *  游戏收藏/取消收藏请求参数
 *  @date: 2025/12/24
 */
data class ProfileCollectEditVo(
    val gameType: Int , // 游戏类型
    val collect: Boolean? // 是否收藏，true:收藏，false:取消收藏，不传则自动转换
)






