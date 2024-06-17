package com.cn.game.sdk2.data.bean

import com.cn.game.sdk2.utils.PinyinUtils
import java.io.Serializable

data class SelectAnnotationBean(
    var money: Int = 0,//压铸的钱
    var select: Boolean = false,
) : Serializable {
    val moneyPinyin: String
        get() {
            return PinyinUtils.toPinyin(money)
        }
}

/**
 * 投注记录用于保存这些动画位置，记录
 */
data class BettingRecordBean(
    /**
     *  动画相对于控件的位置
     */
    var viewXYTemporary: IntArray = IntArray(2),

    /**
     *  动画结束后显示的位置  相对于控件的位置  要点击确定的时候保存
     */
    var viewXYLast: IntArray = IntArray(2),

    /**
     *   用于保存默认临时钱   如果当前结束了要清空 而且点击了确定就要清空~~或者投注失败用于减去确定的钱
     */
    var moneyTemporary: Int = 0,
//    /**
//     *   用于保存默认临时钱   如果下注成功就要清空， 如果失败的话就要就要确定钱减去上次失败的钱然后清空
//     */
//    var moneyTemporaryErr: Int =0,

    /**
     * * 用于保存确定的钱并且游戏结束要清空
     */
    var moneyOkEmpty: Int = 0,

    /**
     * * 用于保存确定的钱用于记录续压
     */
    var moneyOk: Int = 0,


    ) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BettingRecordBean

        if (!viewXYTemporary.contentEquals(other.viewXYTemporary)) return false

        return true
    }

    override fun hashCode(): Int {
        return viewXYTemporary.contentHashCode()
    }
}


/**
 * 历史结果
 */
data class HistoryResultBean(
    var money: String = "50",
    var isShow: Boolean = true,
    val result: List<Int> = listOf(1, 2, 3)
) : Serializable {

    val resultSum: Int
        get() = result.sumOf { it }

    //开奖大小
    val resultSize: String
        get() {
            return if (resultSum > result.size * 3) "big" else "small"
        }

    //开奖单双
    val resultOdd: String
        get() {
            val odd = resultSum % 2 != 0
            return if (odd) "odd" else "even"
        }
}

/**
 * 中奖的区域和钱
 */
data class InPrizeBean(
    var inPrizType: Int = -1,//类型
    var money: Int = 0,//中奖钱
) : Serializable

