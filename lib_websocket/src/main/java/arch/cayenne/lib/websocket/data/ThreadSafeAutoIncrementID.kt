package arch.cayenne.lib.websocket.data

import java.util.concurrent.atomic.AtomicInteger

/**
 * @date: 2025/6/5 14:14
 * @description: 线程安全自增长ID
 */
class ThreadSafeAutoIncrementID(private val max: Int = 0xFF) {
    private val idV: AtomicInteger = AtomicInteger(0x00)
    val id: Int get() = idV.getAndUpdate {
        current -> if (current >= max) 0 else current + 1
    }
}