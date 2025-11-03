package arch.cayenne.lib.base.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * @author: zhangsan
 * @date: 2025/3/17 18:19
 * @description:
 */
abstract class BaseRepository(override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) : IRepository {
    protected val TAG:String by lazy { this::class.java.simpleName }
}