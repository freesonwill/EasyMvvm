package com.walisport.module.search.data.model

import galaxy.common.proto.Common
import java.io.Serializable

/** * 搜索结果中的每日比赛数量
 * @property day 比赛日期，格式为yyyy-MM-dd
 * @property count 当天的比赛数量
 */
data class SearchDailyMatchBean(
    val day: String,  //yyyy-MM-dd
    val count: Int    //比赛数量
): Serializable {
    companion object {
        fun from(resp: Common.DailyMatchCount): SearchDailyMatchBean {
            return SearchDailyMatchBean(
                day = resp.day,
                count = resp.count
            )
        }

        fun fromList(list: List<Common.DailyMatchCount>) = list.map { from(it) }
    }
}