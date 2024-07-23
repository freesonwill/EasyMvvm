package com.cn.game.sdk_new

import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testForeach() {
        println("begin")
        arrayOf(2,3,4).forEachIndexed { index, i ->
            if(index == 1)return //相当于continue
            println("testForeach--->${i}")
        }
        println("end")
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