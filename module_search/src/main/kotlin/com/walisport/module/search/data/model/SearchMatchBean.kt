package com.walisport.module.search.data.model

import galaxy.common.proto.Common
import java.io.Serializable

/** 搜索结果中的比赛信息
 * @property matchId 比赛id
 * @property collect 用户是否收藏
 * @property basicInfo 比赛基本信息
 */
data class SearchMatchBean(
    val matchId: Long,                //比赛id
    val collect: Boolean = false,     //用户是否收藏
    val basicInfo: SearchMatchDetailBean, //比赛基本信息
): Serializable {
    companion object {
        fun from(resp: Common.Match): SearchMatchBean {
            return SearchMatchBean(
                matchId = resp.matchId,
                collect = resp.collect,
                basicInfo = SearchMatchDetailBean.from(resp.basicInfo)
            )
        }

        fun fromList(list: List<Common.Match>) = list.map { from(it) }
    }
}