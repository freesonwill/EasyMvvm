package com.walisport.module.search.data.model

import galaxy.common.proto.Common

/** * 搜索结果中的联赛信息
 * @property id 联赛id
 * @property name 联赛名称
 * @property simpleName 缩写
 * @property icon 联赛icon url
 * @property sportId 球类id
 * @property hot 是否热门
 * @property weight 权重
 * @property color 配色
 * @property season 赛季
 */
data class SearchResultTournamentBean(
    override val id: Int,               //联赛id
    override val name: String,          //联赛名称
    val simpleName: String,             //缩写
    override val icon: String,          //联赛icon url
    val sportId: Int,                   //球类id
    val hot: Boolean,                   //是否热门
    val weight: Int,                    //权重
    override val color: String,         //配色
    val season: String                  //赛季
): SearchResultBaseBean(id, name, icon, color) {
    companion object {
        fun from(resp: Common.Tournament): SearchResultTournamentBean {
            return SearchResultTournamentBean(
                id = resp.id,
                name = resp.name,
                simpleName = resp.simpleName,
                icon = resp.icon,
                sportId = resp.sportId,
                hot = resp.hot,
                weight = resp.weight,
                color = resp.color,
                season = resp.season
            )
        }

        fun fromList(list: List<Common.Tournament>) = list.map { from(it) }
    }
}