package arch.cayenne.module.bet.data

import arch.cayenne.lib.common.utils.ext.IntExt.getMoney

data class ComboMultiBetBean(
    val combo: Int, // 串關次數
    val sumOdds: Int, // 串關後賠率加總
    val count: Int, // 場次組合數量
    var inputMoney: Int = 0,
) {
    val amount: String
        get() = if (inputMoney > 0) {
            (inputMoney * count).getMoney()
        } else {
            0.getMoney()
        }

    val maxWinMoney: String
        get() = if (inputMoney > 0) {
            inputMoney.getMoney(sumOdds)
        } else {
            0.getMoney()
        }
}
