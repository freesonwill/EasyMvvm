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

