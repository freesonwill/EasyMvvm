package com.walisport.module.search.data.model

import galaxy.common.proto.Common

/** * 搜索结果中的比赛直播信息
 * @property clock 走表时间，以秒为单位
 * @property rollClock 是否走表
 * @property period 阶段
 * @property score 比分
 * @property liveVideo 该比赛是否有视频或者直播
 * @property chatRoom 是否开启了聊天室
 * @property viewerCount 观看数量
 * @property clockModified 走表修改时间
 * @property homeScore 主队得分
 * @property awayScore 客队得分
 * @property periodName 阶段名称
 */
data class SearchMatchLiveInfoBean(
    val clock: Int,                //走表时间，以秒为单位
    val rollClock: Boolean,        //是否走表
    val period: String,            //阶段
    val score: String,             //比分
    val liveVideo: Boolean,        //该比赛是否有视频或者直播
    val chatRoom: Boolean,         //是否开启了聊天室
    val viewerCount: Int,          //观看数量
    val clockModified: Long,       //走表修改时间
    val homeScore: Int,            //主队得分
    val awayScore: Int,            //客队得分
    val periodName: String         //阶段名称
) {
    companion object {
        fun from(resp: Common.MatchLiveInfo): SearchMatchLiveInfoBean {
            return SearchMatchLiveInfoBean(
                clock = resp.clock,
                rollClock = resp.rollClock,
                period = resp.period,
                score = resp.score,
                liveVideo = resp.liveVideo,
                chatRoom = resp.chatRoom,
                viewerCount = resp.viewerCount,
                clockModified = resp.clockModified,
                homeScore = resp.homeScore,
                awayScore = resp.awayScore,
                periodName = resp.periodName
            )
        }
    }
}