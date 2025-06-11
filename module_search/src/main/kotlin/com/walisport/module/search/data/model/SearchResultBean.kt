package com.walisport.module.search.data.model

import com.walisport.module.search.data.constants.SearchResultTypeEnum
import java.io.Serializable

/** * 搜索结果数据模型
 * @property type 搜索结果类型
 * @property dataList 结果列表，可能是球员、球队或联赛
 * @property directData 精准匹配数据，可能是球员、球队或联赛
 * @property matchTotal 比赛数量
 * @property matches 比赛信息，不含盘口数据
 * @property dailyCount 每日比赛数量
 */
data class SearchResultBean(
    val type: SearchResultTypeEnum,                             //0-无结果  1-结果列表  2-精准匹配球员  3-精准匹配球队 4-精准匹配联赛
    val dataList: List<SearchResultBaseBean>? = null,           //结果列表，可能是球员、球队或联赛
    val directData: SearchResultBaseBean? = null,               //精准匹配数据，可能是球员、球队或联赛
    val matchTotal: Int? = null,                                //比赛数量
    val matches: List<SearchMatchBean>? = null,                 //比赛信息，不含盘口数据
    val dailyCount: List<SearchDailyMatchBean>? = null          //每日比赛数量
): Serializable