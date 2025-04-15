package arch.cayenne.lib.common

import arch.cayenne.lib.common.utils.ext.IntExt.getMoney
import arch.cayenne.lib.common.utils.ext.IntExt.getRate
import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import org.junit.Test

import org.junit.Assert.*

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
        println("0.101".toValue())    // 10
        println("0.10".toValue())   // 10
        println("10".toValue())     // 1000
        println("12.34".toValue())  // 1234
        println("0.01".toValue())   // 1

        println("int multiply int")  // 0
        println(198.getRate(1))    // 1.98
        println(1000.getRate(1))   // 10.00
        println(10.getRate(1))     // 0.1
        println(1.getRate(1))      // 0.01
        println(1234.getRate(1))   // 12.34
        println(200.getRate(2))    // 4
        println(255.getRate(3))    // 7.65
    }
}