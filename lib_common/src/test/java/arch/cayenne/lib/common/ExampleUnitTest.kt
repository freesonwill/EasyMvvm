package arch.cayenne.lib.common

import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
//        println("get money from int: ")
//        println(198.getMoney())   // 1.98
//        println(1000.getMoney())  // 10
//        println(10.getMoney())    // 0.1
//        println(1.getMoney())     // 0.1
//        println(1234.getMoney())  // 12.34
//        println(100.getMoney())   // 1

        println("get int from string: ")

        println("int multiply int")  // 0
        println(198.getOdds(1))    // 1.98
        println(1000.getOdds(1))   // 10.00
        println(10.getOdds(1))     // 0.1
        println(1.getOdds(1))      // 0.01
        println(1234.getOdds(1))   // 12.34
        println(200.getOdds(2))    // 4
        println(255.getOdds(3))    // 7.65
    }
}