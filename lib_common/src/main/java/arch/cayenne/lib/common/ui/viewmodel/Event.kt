package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

open class Event<out T>(
    private val content: T
) {
    private val handledObservers = mutableSetOf<LifecycleOwner>()

    fun getContentIfNotHandled(owner: LifecycleOwner): T? {
        return if (handledObservers.contains(owner)) {
            null
        } else {
            handledObservers.add(owner)
            content
        }
    }

    fun peekContent(): T = content
}

fun <T>LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    key: LifecycleOwner,
    onEventUnhandledContent: (T) -> Unit
) {
    observe(owner) { event ->
        event?.getContentIfNotHandled(key)?.let {
            onEventUnhandledContent(it)
        }
    }
}