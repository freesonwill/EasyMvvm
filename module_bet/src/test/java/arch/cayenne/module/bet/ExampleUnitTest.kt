package arch.cayenne.module.bet

import arch.cayenne.lib.common.utils.ext.CombinationExt.combination
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val oddsList1 = listOf(206,161,125,295,290)
        val oddsList = listOf(206,161,125,295,290,206,161,125,295,290,206,161,125,295,290,206,161,125,295,290)
        calculateOdds(oddsList1)
        calculateOdds2(oddsList)

        calculateCombinationOdds(oddsList,20).let(::println)
        val base = BigDecimal("354668912500")
        val result = base.pow(4)
        println(result)

        calculateCombinationOdds(oddsList1,2).let(::println)
        calculateCombinationOdds(oddsList,5).let(::println)
    }

    private fun calculateCombinationOdds(oddsList:List<Int>, k:Int):Int {
        val combinationData = oddsList.combination(k)
        val sumOdds = combinationData.sumOf { list ->
            if(list.isEmpty()) 0.0
            else list.fold(1.0) { acc, odds -> acc * (odds*0.01) }// 计算乘积并且每次除以100
        }
        return (sumOdds* 100).toInt()
    }

    private fun calculateOdds2(oddsList:List<Int>){
        val k = oddsList.size
        val combinationData = oddsList.combination(k)
        val sumOdds = combinationData.sumOf { it.fold(1.00) { acc, l -> 0.01* acc * l } }
        "calculateOdds2--sumOdds2--->$sumOdds".let(::println)
    }

    private fun calculateOdds(oddsList:List<Int>){
        val k = oddsList.size
        val combinationData = oddsList.combination(k)
        val sumOdds = combinationData.sumOf { it.fold(1L) { acc, l -> 1L* acc * l } }
        val sumOdds2 = getScaleOdds(sumOdds,(combinationData.first().size - 1) * 2)
        val a = 2037187750
        val b = oddsList[0] * oddsList[1] * oddsList[2]
        val c = oddsList[0] * oddsList[1] * oddsList[2]* oddsList[3]
        val d = oddsList[0] * oddsList[1] * oddsList[2]* oddsList[3]* oddsList[4]
        val e = c* oddsList[4]
        val f = 1222996250* oddsList[4]
        "calculateOdds---sumOdds2--->$sumOdds2--sumOdds:$sumOdds--b:$b--c:$c--d:$d,e:$e,f:$f".let(::println)
    }

    /**
     * 10000/10^2 = 100
     */
    private fun getScaleOdds(odds:Long,scale: Int): Int {
        val divisor = BigDecimal.TEN.pow(scale)
        return odds.toBigDecimal().divide(divisor, scale, RoundingMode.DOWN).toInt()
    }
}