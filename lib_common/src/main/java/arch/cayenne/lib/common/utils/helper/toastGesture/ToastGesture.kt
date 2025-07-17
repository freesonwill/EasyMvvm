package arch.cayenne.lib.common.utils.helper.toastGesture

import android.view.MotionEvent
import android.view.View

abstract class ToastGesture {

    // 用於強制移除 Toast 的回調
    private var forceRemoveListener: (() -> Unit)? = null

    abstract fun onTouch(view: View, event: MotionEvent): Boolean
    abstract fun canAutoRemove(): Boolean
    fun setForceRemoveListener(listener: () -> Unit) {
        forceRemoveListener = listener
    }
    protected fun forceRemove() {
        forceRemoveListener?.invoke()
    }
}