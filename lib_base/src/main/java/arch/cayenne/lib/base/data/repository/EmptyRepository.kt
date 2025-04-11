package arch.cayenne.lib.base.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:49
 * @description:
 */
class EmptyRepository : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
}