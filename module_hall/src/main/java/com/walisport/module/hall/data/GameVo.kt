package com.walisport.module.hall.data

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
