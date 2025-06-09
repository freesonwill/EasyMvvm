package arch.cayenne.lib.socket

import arch.cayenne.lib.websocket.data.ThreadSafeAutoIncrementID
import org.junit.Test

import org.junit.Assert.*
import kotlin.concurrent.thread

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
    fun testAutoIncrementID() {
        val autoIncrementID = ThreadSafeAutoIncrementID(0xFF)
        val threads = mutableListOf<Thread>()
        println("AutoIncrementID Test Start")
        for( i in 0 until 100) {
            threads.add(thread(start = false) {
               for( j in 0 until 100) {
                   val id = autoIncrementID.id
                   println("--->${Thread.currentThread().id}--->ID: $id")
                   Thread.sleep(10)
               }
           })
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        println("AutoIncrementID Test End")
    }
}