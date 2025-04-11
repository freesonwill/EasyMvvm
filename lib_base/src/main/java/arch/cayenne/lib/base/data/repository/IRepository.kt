package arch.cayenne.lib.base.data.repository

import kotlinx.coroutines.CoroutineScope

/**
 * @author: zhangsan
 * @date: 2025/3/14 14:00
 * @description:
 */
interface IRepository {
    val scope: CoroutineScope
}