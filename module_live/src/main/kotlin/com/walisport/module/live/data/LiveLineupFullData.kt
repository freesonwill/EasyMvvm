package com.walisport.module.live.data

import com.walisport.module.live.data.model.MatchLineupDetail
import com.walisport.module.live.data.model.Player
import com.walisport.module.live.data.model.PlayerIncident
import com.walisport.module.live.data.model.PlayerInfo
import galaxy.client.proto.Sloth

data class LiveLineupFullData(
    val lineupData: MatchLineupDetail,
    )

fun Sloth.MatchLineupDetail.toLineupData(): LiveLineupFullData {
    val lineupData: MatchLineupDetail
    val home = mutableListOf<Player>() // 主队阵型球员列表
    val away = mutableListOf<Player>()   // 客队阵型球员列表
    this.homeOrBuilderList.forEach { homeOr ->
        val incident = mutableListOf<PlayerIncident>()   // 客队阵型球员列表
        homeOr.incidentsList.forEach { incidentData ->
            incident.add(
                PlayerIncident(
                    type = incidentData.type,             // 事件类型（参考技术类型）
                    time = incidentData.time,               // 事件发生时间（含加时时间，如 'A+B'）
                    belong = incidentData.belong,            // 发生方，0-中立、1-主队、2-客队
                    homeScore = incidentData.homeScore,         // 主队比分
                    awayScore = incidentData.awayScore,         // 客队比分
                    player = PlayerInfo(
                        id = incidentData.player.id,
                        name = incidentData.player.name
                    ),     // 球员信息
                    assist1 = PlayerInfo(
                        id = incidentData.assist1.id,
                        name = incidentData.assist1.name
                    ),     // 助攻球员1
                    assist2 = PlayerInfo(
                        id = incidentData.assist2.id,
                        name = incidentData.assist2.name
                    ),     // 助攻球员2
                    inPlayer = PlayerInfo(
                        id = incidentData.inPlayer.id,
                        name = incidentData.inPlayer.name
                    ),     // 换上球员
                    outPlayer = PlayerInfo(
                        id = incidentData.outPlayer.id,
                        name = incidentData.outPlayer.name
                    ),     // 换下球员
                    reasonType = incidentData.reasonType,        // 红黄牌、换人事件原因
                )
            )
        }
        home.add(
            Player(
                id = homeOr.id,             // 球员id
                teamId = homeOr.teamId,         // 球队id
                first = homeOr.first,          // 是否首发，1-是、0-否
                captain = homeOr.captain,        // 是否队长，1-是、0-否
                name = homeOr.name,           // 球员名称
                logo = homeOr.logo,          // 球员logo
                nationalLogo = homeOr.nationalLogo, // 球员logo(国家队)
                shirtNumber = homeOr.shirtNumber,    // 球衣号
                position = homeOr.position,       // 球员位置，F前锋、M中场、D后卫、G守门员
                x = homeOr.x,              // 阵容x坐标，总共100
                y = homeOr.y,              // 阵容y坐标，总共100
                rating = homeOr.rating,       // 评分，10为满分
                incidents = incident // 球员事件列表
            )
        )

    }
    this.awayOrBuilderList.forEach { awayOr ->
        val incident = mutableListOf<PlayerIncident>()   // 客队阵型球员列表
        awayOr.incidentsList.forEach { incidentData ->
            incident.add(
                PlayerIncident(
                    type = incidentData.type,             // 事件类型（参考技术类型）
                    time = incidentData.time,               // 事件发生时间（含加时时间，如 'A+B'）
                    belong = incidentData.belong,            // 发生方，0-中立、1-主队、2-客队
                    homeScore = incidentData.homeScore,         // 主队比分
                    awayScore = incidentData.awayScore,         // 客队比分
                    player = PlayerInfo(
                        id = incidentData.player.id,
                        name = incidentData.player.name
                    ),     // 球员信息
                    assist1 = PlayerInfo(
                        id = incidentData.assist1.id,
                        name = incidentData.assist1.name
                    ),     // 助攻球员1
                    assist2 = PlayerInfo(
                        id = incidentData.assist2.id,
                        name = incidentData.assist2.name
                    ),     // 助攻球员2
                    inPlayer = PlayerInfo(
                        id = incidentData.inPlayer.id,
                        name = incidentData.inPlayer.name
                    ),     // 换上球员
                    outPlayer = PlayerInfo(
                        id = incidentData.outPlayer.id,
                        name = incidentData.outPlayer.name
                    ),     // 换下球员
                    reasonType = incidentData.reasonType,        // 红黄牌、换人事件原因
                )
            )
        }
        away.add(
            Player(
                id = awayOr.id,             // 球员id
                teamId = awayOr.teamId,         // 球队id
                first = awayOr.first,          // 是否首发，1-是、0-否
                captain = awayOr.captain,        // 是否队长，1-是、0-否
                name = awayOr.name,           // 球员名称
                logo = awayOr.logo,          // 球员logo
                nationalLogo = awayOr.nationalLogo, // 球员logo(国家队)
                shirtNumber = awayOr.shirtNumber,    // 球衣号
                position = awayOr.position,       // 球员位置，F前锋、M中场、D后卫、G守门员
                x = awayOr.x,              // 阵容x坐标，总共100
                y = awayOr.y,              // 阵容y坐标，总共100
                rating = awayOr.rating,       // 评分，10为满分
                incidents = incident // 球员事件列表
            )
        )
    }
    lineupData = MatchLineupDetail(
        confirmed = this.confirmed,           // 正式阵容，1-是、0-否
        homeFormation = this.homeFormation,       // 主队阵型
        awayFormation = this.awayFormation,      // 客队阵型
        homeColor = this.homeColor,         // 主队球衣颜色
        awayColor = this.awayColor,           // 客队球衣颜色
        home = home,        // 主队阵型球员列表
        away = away,      // 客队阵型球员列表
        homeId = this.homeId,           // 主队Id
        homeLogo = this.homeLogo,          // 主队logo
        awayId = this.awayId,          // 客队Id
        awayLogo = this.awayLogo          // 客队logo
    )
    return LiveLineupFullData(
        lineupData
    )
}