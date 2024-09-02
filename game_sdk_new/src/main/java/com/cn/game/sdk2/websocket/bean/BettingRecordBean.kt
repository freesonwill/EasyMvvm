package com.cn.game.sdk2.websocket.bean

import com.cn.game.sdk2.websocket.constants.BettingStatus
import java.io.Serializable

/**
 * 每次点击下注就传入该对象
 */
data class BettingRecordBean(

    /**
     * 注区
     */
    var bettingArea: Betting,

    /**
     *  动画相对于控件的位置
     */
    var viewXYTemporary: FloatArray = FloatArray(2),

    /**
     * * 下注金额
     *  - 传入时：该字段单位是元
     *  - 收到时：该字段单位是分，所以需要缩小100倍
     */
    var money: Int = 0,

    var state: BettingStatus = BettingStatus.TEMP

) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BettingRecordBean

        return viewXYTemporary.contentEquals(other.viewXYTemporary)
    }

    override fun hashCode(): Int {
        var result = bettingArea.hashCode()
        result = 31 * result + viewXYTemporary.contentHashCode()
        result = 31 * result + money
        return result
    }
}