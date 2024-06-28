package com.cn.game.sdk_new

import com.cn.game.sdk2.websocket.bean.RoundInfoBean
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
        assertEquals(4, 2 + 2)
    }

    @Test
    fun isLeopardTest (){
        var ab = RoundInfoBean("1", listOf(1,2,3,4),1,true,true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard,false)

        ab = RoundInfoBean("1", listOf(1,1,1),1,true,true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard,true)

        ab = RoundInfoBean("1", listOf(1,1),1,true,true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard,true)

        ab = RoundInfoBean("1", listOf(1),1,true,true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard,true)

        ab = RoundInfoBean("1", listOf(1,2),1,true,true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard,false)

        ab = RoundInfoBean("1", listOf(),1,true,true)
        ab.isLeopard.let(::println)
        assertEquals(ab.isLeopard,false)

    }

    inline val RoundInfoBean.isLeopard:Boolean get() = run {
        if(performs.isEmpty()) return@run false
        performs.forEachIndexed { index,item->
            if(index in 1..2) {
                if(item != performs[index-1]) {
                    return@run false
                }
            }
        }
        return@run true
    }
}