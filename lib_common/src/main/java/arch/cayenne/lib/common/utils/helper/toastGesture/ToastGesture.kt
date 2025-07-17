package arch.cayenne.lib.common.utils.helper.toastGesture

import android.view.MotionEvent
import android.view.View

abstract class ToastGesture {

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