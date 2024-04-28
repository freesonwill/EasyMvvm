package com.cn.game.sdk.bean

import java.io.Serializable

data class SelectAnnotationBean(
    var money: Int=0,//压铸的钱
    var select: Boolean=false,


): Serializable


/**
 * 历史结果
 */
data class HistoryResultBean(
    var money: String="50",
    var isShow:Boolean=true

    ): Serializable
