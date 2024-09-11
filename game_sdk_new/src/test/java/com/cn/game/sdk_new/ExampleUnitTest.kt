package com.cn.game.sdk_new

import com.cn.game.sdk2.utils.ext.CommonExt.formatRealMoney
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    @Test
    fun testShowMoneyFormat() {
        println("begin")
        showMoneyFormat((100 * 1010f).toInt()).let(::println)
        assert(showMoneyFormat((100 * 100.25f).toInt())=="100+")
        assert(showMoneyFormat((100 * 101.25f).toInt())=="101+")
        assert(showMoneyFormat((100 * 1010f).toInt())=="1.01K")
        assert(showMoneyFormat((100 * 1009f).toInt())=="1K+")
        assert(showMoneyFormat((100 * 1090f).toInt())=="1.09K")
        assert(showMoneyFormat((100 * 1000f).toInt())=="1K")
        assert(showMoneyFormat((100 * 10100f).toInt())=="1.01W")
        assert(showMoneyFormat((100 * 10000f).toInt())=="1W")
        println("end")
    }


    private fun showMoneyFormat(money: Int): String {
        val moneyInt = money / 100f
        val sb = StringBuilder()
        val units = arrayOf(10000 to "W", 1000 to "K", 1 to "")
        for (i in units.indices) {
            val unit = units[i].first
            val unitStr = units[i].second
            if (moneyInt >= unit) {
                (moneyInt / unit).let {
                    if (it.compareTo(it.toInt()) == 0) sb.append(it.toInt()).append(unitStr)
                    else sb.append(it).append(unitStr)
                }
            }
            if (sb.length > 5) {
                sb.clear()
                sb.append((moneyInt / unit).toInt()).append("${unitStr}+")
                return sb.toString()
            }
            if(sb.isNotEmpty()) return sb.toString()
        }
        throw IllegalStateException("showMoneyFormat:${money} error")
    }

    class Person {
        var name: String by PersonName()
        var age: Int by Delegates.observable(0) { prop, oldV, newV ->
            println("$oldV->$newV")
        }
    }

    class PersonName {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return "$thisRef, thank you for delegating '${property.name}' to me!"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }

    @Test
    fun testDelegate() {
        val p = Person()
        /*        p.name.let(::println)
                p.name = "lisi"
                p.name.let(::println)*/
        p.age = 30
    }

    @Test
    fun testFlow() {
        runBlocking {
            flow {
                emit(1)
                delay(90)
                emit(2)
                delay(90)
                emit(3)
                delay(1010)
                emit(4)
                delay(10)
                emit(5)
                delay(100)
                emit(6)
                delay(1000)
                emit(7)
            }.debounce(1000).collect {
                print(it)
            }
        }
    }


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun isLeopardTest() {
        var ab = RoundInfoBean("1", listOf(1, 2, 3, 4), 1, true, true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard, false)

        ab = RoundInfoBean("1", listOf(1, 1, 1), 1, true, true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard, true)

        ab = RoundInfoBean("1", listOf(1, 1), 1, true, true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard, true)

        ab = RoundInfoBean("1", listOf(1), 1, true, true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard, true)

        ab = RoundInfoBean("1", listOf(1, 2), 1, true, true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard, false)

        ab = RoundInfoBean("1", listOf(), 1, true, true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard, false)

    }

    inline val RoundInfoBean.isLeopard: Boolean
        get() = run {
            if (performs.isEmpty()) return@run false
            performs.forEachIndexed { index, item ->
                if (index in 1..2) {
                    if (item != performs[index - 1]) {
                        return@run false
                    }
                }
            }
            return@run true
        }
}