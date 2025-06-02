package com.walisport.module.search.data.model

import com.walisport.module.search.data.constants.PlayerPositionEnum
import galaxy.common.proto.Common

/** * 搜索结果中的球员信息
 * @property id 球员id
 * @property name 球员名称
 * @property icon 球员头像
 * @property number 球员号码
 * @property position 球员位置，F-前锋、M-中场、D-后卫、G-守门员
 * @property teamId 球队id
 * @property teamName 球队名称
 * @property tournamentId 联赛id
 * @property tournamentName 联赛名称
 * @property tournamentShortName 联赛简称
 * @property color 联赛配色
 */
data class SearchResultPlayerBean(
    override val id: Int,               //球员id
    override val name: String,          //名称
    override val icon: String,          //头像
    val number: Int,                    //球员号码
    val position: PlayerPositionEnum,       //球员位置，F-前锋、M-中场、D-后卫、G-守门员
    val teamId: Int,                    //球队id
    val teamName: String,               //球队名称
    val tournamentId: Int,              //联赛id
    val tournamentName: String,         //联赛名称
    val tournamentShortName: String,    //联赛简称
    override val color: String          //联赛配色
): SearchResultBaseBean(id, name, icon, color) {
    companion object {
        fun from(resp: Common.SearchPlayer): SearchResultPlayerBean {
            return SearchResultPlayerBean(
                id = resp.id,
                name = resp.name,
                icon = resp.icon,
                color = resp.color,
                tournamentId = resp.tournamentId,
                tournamentName = resp.tournamentName,
                tournamentShortName = resp.tournamentShortName,
                teamId = resp.teamId,
                teamName = resp.teamName,
                number = resp.number,
                position = PlayerPositionEnum.fromCode(resp.position)
            )
        }

        fun fromList(list: List<Common.SearchPlayer>) = list.map { from(it) }
    }
}