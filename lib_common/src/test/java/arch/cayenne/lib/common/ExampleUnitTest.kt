package arch.cayenne.lib.common

import arch.cayenne.lib.common.utils.ext.CombinationExt
import arch.cayenne.lib.common.utils.ext.CombinationExt.combinations
import org.junit.Test
import kotlin.system.measureTimeMillis


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        /*val list = MutableList(20){ it+1 }
        val isMock = Build.FINGERPRINT == "unknown" || Build.FINGERPRINT == "robolectric"
        "isMock--->$isMock,FingerPRint:${Build.FINGERPRINT}".let { println(it) }
        val t1 = System.currentTimeMillis()
        measureTimeAndMemory{
            for(k in list.size downTo 0) list.combinationsRecursion(k)
        }
        val t2 = System.currentTimeMillis()
        "combinationsRecursion(${list.size}) costMills:${t2-t1-50}".logd()
        measureTimeAndMemory{
            for(k in list.size downTo 0) list.combinations(k)
        }
        val t3 = System.currentTimeMillis()
        "combinations(${list.size}) costMills:${t3-t2-50}".logd()*/
        /*val oddsList = MutableList(20){ it+1 }

        for(k in oddsList.size downTo 0){
            val combinationData = oddsList.combinations(k)
            val cNk = CollectionExt.cNk(oddsList.size,k).toInt()
            "combinationData:${combinationData.size}--${cNk},k:$k".logd()
            assert(combinationData.size == cNk)
        }*/
        CombinationExt.cNK(33,16).let { println(it) }
        CombinationExt.cNK(34,15).let { println(it) }
        CombinationExt.cNK(34,16).let { println(it) }
        CombinationExt.cNK(34,17).let { println(it) }
        CombinationExt.cNK(34,18).let { println(it) }
    }

    /**
     * 测量函数执行的耗时和内存消耗
     */
    private inline fun <T> measureTimeAndMemory(block: () -> T): T {
        System.gc()
        Thread.sleep(50)

        val runtime = Runtime.getRuntime()
        val memBefore = runtime.totalMemory() - runtime.freeMemory()

        var result: T
        val time = measureTimeMillis {
            result = block()
        }

        val memAfter = runtime.totalMemory() - runtime.freeMemory()

        println("Time: ${time}ms")
        println("JVM heap memory used: ${memAfter - memBefore} bytes")

        return result
    }

    fun calculateCombinationOdds(oddsList:List<Int>, k:Int):Int {
        val combinationData = oddsList.combinations(k)
        val sumOdds = combinationData.sumOf { list ->
            if(list.isEmpty()) 0.0
            else list.fold(1.0) { acc, odds -> acc * (odds*0.01) }// 计算乘积并且每次除以100
        }
        return (sumOdds* 100).toInt()
    }

}