package arch.cayenne.module.bet.data

import arch.cayenne.lib.common.utils.ext.IntExt.getMoney
import arch.cayenne.lib.common.utils.ext.StringExt.toValue

/***
 * 有三場比賽欲串關，則有3串1、3串2、3串3，共三個串關方式
 * 以3串2為例，combo則為2，count為3 (1vs2+1vs3+2vs3)，odds為count的三場比賽加總
 * @param combo 串關次數 ex. 4串1關 的1關
 * @param sumOdds 串關後賠率加總
 * @param count 場次組合數量 ex. 3串2則有三場
 * @param inputMoney 使用者輸入的金額
 * @param minAmount 最小下注金額
 * @param maxAmount 最大下注金額
 */
data class ComboMultiBetBean(
    val combo: Int, // 串關次數
    val sumOdds: Int, // 串關後賠率加總
    val count: Int, // 場次組合數量
    var inputMoney: Int = 0,
    val minAmount: Int,
    val maxAmount: Int
) {
    val amount: Int
        get() = inputMoney * count

    val maxWinMoney: Int
        get() = inputMoney.getMoney(sumOdds).toValue()
}
