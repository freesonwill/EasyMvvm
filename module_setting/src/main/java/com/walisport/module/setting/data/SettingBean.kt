package com.walisport.module.setting.data

/**
 * 系统配置
 */

data class SettingBean(
    val oddType: Int = 0,                 //赔率类型, 0-欧盘 1-香港盘
    val systemGoal: NotifyMatchType,      //系统通知-进球
    val systemKickOff: NotifyMatchType,   //系统通知-开球
    val appGoal: NotifyMatchType,         //app内通知-开球
    val background: Int,                  //背景设置
    val lang: String = "ZH"               //语言
)

data class NotifyMatchType(
    var betMatch: Boolean = false,        //已投注的比赛通知
    var collectMatch: Boolean = false,    //已收藏的比赛通知
    var allMatch: Boolean = false         //所有的比赛通知
)
