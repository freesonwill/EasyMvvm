package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

/**
 * @author: KC
 * @date: 2025/05/26 13:10
 * @description: 把livedata的內容包成Event，防止資料倒灌，辨識是否使用過使用LifecycleOwner
 */

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

/**
 * @param owner LiveData 的observe生命週期依附者
 * @param key 用來辨識是否被observe過，避免資料倒灌
 * 與上方@owner分開是因為目前BaseFragment keepViewOnNavigation = true時，view在離開頁面時會被keep，但是
 * 該view的viewLifecycleOwner會被回收，等到回到原始頁面時view是原本被keep的view，但是viewLifecycleOwner會是新的，
 * 所以此時viewLifecycleOwner因為是新的所以會再被原本的資料倒灌一次
 * 建議如果因為保持目前頁面切換回來的資料倒灌，@key使用fragment的LifecycleOwner，因為當前的fragment依舊存在，所以LifecycleOwner會存在Event 的set中避免再次收到
 * */
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