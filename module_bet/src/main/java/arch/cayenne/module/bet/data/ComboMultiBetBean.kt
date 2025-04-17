package arch.cayenne.module.bet.data

import arch.cayenne.lib.common.utils.ext.IntExt.getMoney
import arch.cayenne.lib.common.utils.ext.StringExt.toValue

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
