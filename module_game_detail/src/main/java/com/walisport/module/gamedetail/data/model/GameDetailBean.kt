package com.walisport.module.gamedetail.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDetailBean(
    val id: Long,                               // 游戏ID
    val name: String,                           // 游戏名称
    val avatar: String,                         // 游戏缩略图
    val online: Int,                            // 在线人数
    val reward: Double,                         // 返奖率
    val hasMore: Boolean,                       // 更多数据
    val collect: Boolean,                       // 是否被收藏
    val score: Double,                          // 评分
    val comments: Int,                          // 评论人数
    val maxOdds: Int,                           // 最高返奖赔率
    val type: Int,                              // 游戏类型
    val supplier: String,                       // 供应商名称
    val tryIt: Boolean,                         // 是否提供试玩
    val materials: String,                     // 游戏介绍物料
    val currency: List<CurrencyInfoBean> ?       // 支持货币
) : Parcelable