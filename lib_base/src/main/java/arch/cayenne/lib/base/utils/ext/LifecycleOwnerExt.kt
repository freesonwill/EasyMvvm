package arch.cayenne.lib.base.utils.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @date: 2025/12/22 00:08
 * @description:LifecycleOwner的扩展函数
 */

fun LifecycleOwner.launch(
    state: Lifecycle.State? = null,
    lifecycleScope: LifecycleCoroutineScope = this.lifecycleScope,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return if (state == null) lifecycleScope.launch(block = block, context = context, start = start)
    else let {
        var job: Job? = null
        lifecycleScope.launch {
            repeatOnLifecycle(state) {
                lifecycleScope.launch(context,start,block)
                job?.cancel()
                job = null
            }
        }.also { job = it }
    }
}