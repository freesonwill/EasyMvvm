package arch.cayenne.lib.base.data.repository

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent

/**
 * @author: zhangsan
 * @date: 2025/3/14 14:00
 * @description:
 */
interface IRepository: KoinComponent {
    val scope: CoroutineScope
}