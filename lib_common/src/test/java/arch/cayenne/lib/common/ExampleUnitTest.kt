package arch.cayenne.lib.common

import arch.cayenne.lib.common.utils.ext.SportIntExt
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
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
        println(1000000L.getFormalMoney())   // 10.00

    }
}