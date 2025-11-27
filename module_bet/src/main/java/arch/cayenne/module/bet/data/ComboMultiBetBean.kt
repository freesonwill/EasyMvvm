package arch.cayenne.module.bet.data

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.module.bet.R

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
    val serialValue: Int = 1, // 多少串一關，0為全串關，-1为超级组合
    val comboK: Int = 1, // 3串2的3
    val comboV: Int = 1, // 3串2的2
    var sumOdds: Int, // 串關後賠率加總
    var odds: Int,
    val count: Int = 1, // 場次組合數量
    var inputMoney: Long = 0,
    val minAmount: Long,
    val maxAmount: Long,
) {
    companion object {
        const val SERIAL_VALUE_SUPER = -1 //超级组合
        const val SERIAL_VALUE_ALL = 0 //全串關
    }

    fun hasSetMoney() = inputMoney != 0L
    val amount: Long
        get() = inputMoney * count

    val maxWinMoney: Long
        get() = inputMoney.getMoney(odds * count).toMoney()

    val isSuperCombo get() = serialValue == SERIAL_VALUE_SUPER

    fun title():String {
        return when {
            isSuperCombo -> R.string.title_combo_bet_super.getString()
            else -> R.string.title_combo_bet_odds.getString(comboK,comboV)
        }
    }

    fun titleTips():String{
        return when {
            comboV == 1 -> {
                R.string.title_combo_bet_detail_tips.getString(
                    title(),
                    R.string.title_combo_bet_odds.getString(comboK,comboV)
                )
            }
            else ->
                R.string.title_combo_bet_detail_tips.getString(
                    title(),
                    ((if(isSuperCombo) 1 else 2)..comboK).joinToString("、") { k ->
                        if (k == 1) arch.cayenne.lib.res.R.string.title_single_bet.getString()
                        else R.string.title_combo_bet_odds.getString(k, 1)
                    }
                )
        }
    }
}

data class ComboMultiBetOddsBean(
    val serialValue: Int = 1,
    val sumOdds: Int
)
