package arch.cayenne.lib.common.utils.ext

import kotlinx.coroutines.launch

/**
 *
 * @date: 2025/12/18 21:46
 * @description:
 */

fun <T> kotlinx.coroutines.flow.Flow<T>.collectIn(
    scope: kotlinx.coroutines.CoroutineScope ,
    collector: suspend (T) -> Unit
) {
    scope.launch {
        this@collectIn.collect { value ->
            collector(value)
        }
    }
}