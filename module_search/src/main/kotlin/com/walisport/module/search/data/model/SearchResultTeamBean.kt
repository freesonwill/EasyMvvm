package com.walisport.module.search.data.model

import galaxy.common.proto.Common

/** * 搜索结果中的球队信息
 * @property id 球队id
 * @property name 球队名称
 * @property icon 球队图标
 * @property tournamentId 联赛id
 * @property tournamentName 联赛名称
 * @property tournamentShortName 联赛简称
 * @property color 联赛配色
 * @property rank 排名
 * @property win 获胜场次
 * @property lose 失败场次
 */
data class SearchResultTeamBean(
    override val id: Int,               //球队id
    override val name: String,          //名称
    override val icon: String,          //图标
    val tournamentId: Int,              //联赛id
    val tournamentName: String,         //联赛名称
    val tournamentShortName: String,    //联赛简称
    override val color: String,         //联赛配色
    val rank: Int,                      //排名
    val win: Int,                       //获胜场次
    val lose: Int                       //失败场次
): SearchResultBaseBean(id, name, icon, color) {
    companion object {
        fun from(resp: Common.SearchTeam): SearchResultTeamBean {
            return SearchResultTeamBean(
                id = resp.id,
                name = resp.name,
                icon = resp.icon,
                color = resp.color,
                tournamentId = resp.tournamentId,
                tournamentName = resp.tournamentName,
                tournamentShortName = resp.tournamentShortName,
                rank = resp.rank,
                win = resp.win,
                lose = resp.lose
            )
        }

        fun fromList(list: List<Common.SearchTeam>) = list.map { from(it) }
    }
}