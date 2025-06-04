package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
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
    // 使用 HashMap 來儲存已處理的觀察者及其對應的 LifecycleObserver
    // Key: 用來標識事件是否被處理的 LifecycleOwner
    // Value: 註冊到 Key 上的 LifecycleObserver，用於在 Key 銷毀時自動清理
    /**
     * 備註：在SharedViewModel中，child fragment會創建observer觀察來自parent的livedata，一旦這個在parent ViewModel中的livedata被啟動，
     * 那event的handledObservers會被加入child fragment的LifecycleOwner，但是當child fragment被destroy時，
     * 因為這個liva data event還是存在於parent，所以會hold住已經destroy的child fragment lifecycleOwner，所以需要執行removeHandledObserver移除
     * **/
    private val handledObservers = mutableMapOf<LifecycleOwner, LifecycleObserver>()

    fun getContentIfNotHandled(owner: LifecycleOwner): T? {
        if (owner.lifecycle.currentState == androidx.lifecycle.Lifecycle.State.DESTROYED) {
            // 如果 keyOwner 已銷毀，從 map 中移除 (如果存在的話)
            removeHandledObserver(owner)
            return null
        }
        return if (handledObservers.contains(owner)) {
            null
        } else {
            // 創建一個 LifecycleObserver 來監聽 keyOwner 的銷毀事件
            val lifecycleObserver = object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    // 當 keyOwner 被銷毀時，從 map 中移除它
                    // 並同時移除對 keyOwner 生命週期的觀察
                    removeHandledObserver(owner)
                }
            }
            owner.lifecycle.addObserver(lifecycleObserver)
            handledObservers[owner] = lifecycleObserver
            content
        }
    }
    /**
     * 移除觀察者，例如在 Fragment 的 onDestroyView 中，如果 key 是 viewLifecycleOwner
     * 或者在特殊情況下需要重置狀態時。
     * 這個方法現在也負責移除 LifecycleObserver。
     */
    private fun removeHandledObserver(keyOwner: LifecycleOwner) {
        // 從 map 中移除 keyOwner，並獲取其對應的 lifecycleObserver
        val observerToRemove = handledObservers.remove(keyOwner)
        observerToRemove?.let {
            // 如果找到了 observer，則從 keyOwner 的生命週期中移除它
            keyOwner.lifecycle.removeObserver(it)
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