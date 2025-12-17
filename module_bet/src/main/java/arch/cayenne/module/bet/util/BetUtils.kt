package arch.cayenne.module.bet.util

import arch.cayenne.lib.common.utils.ext.CombinationExt.combination
import arch.cayenne.module.bet.data.ComboMultiBetBean.Companion.SERIAL_VALUE_SUPER

/**
 * @date: 2025/12/12 17:13
 * @description:
 */
object BetUtils {

    /**
     * 计算组合的赔率
     *
     * @param oddsList: 赔率列表
     * @param k:        几个为一组
     * eg:
     * input: oddsList:[206,161,125,295,290], k:2
     * output: 4524
     * @return
     */
    fun calculateCombinationOdds(oddsList:List<Int>, k:Int):Int {
        val combinationData = oddsList.combination(k)
        val sumOdds = combinationData.sumOf { list ->
            if(list.isEmpty()) 0.0
            else list.fold(1.0) { acc, odds -> acc * (odds*0.01) }// 计算乘积并且每次除以100
        }
        return (sumOdds* 100).toInt()
    }

    fun isSuperCombo(serialValue:Int) = serialValue == SERIAL_VALUE_SUPER
}