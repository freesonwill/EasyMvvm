package arch.cayenne.lib.qyplayer.cache

import com.xxx.qyplayer.QYPlayer
import java.util.concurrent.ConcurrentHashMap

object PlayerCache {
    private val cache = ConcurrentHashMap<QYPlayer, Int>()

    fun put(player: QYPlayer, count: Int) = cache.put(player, count)
    fun get(player: QYPlayer): Int? = cache[player]
    val keys: Set<QYPlayer> get() = cache.keys
    fun clear() = cache.clear() // 可选：清理缓存
}