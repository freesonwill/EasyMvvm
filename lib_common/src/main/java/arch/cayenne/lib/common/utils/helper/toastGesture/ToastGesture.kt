package arch.cayenne.lib.common.utils.helper.toastGesture

import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper

abstract class ToastGesture {

    // 用於強制移除 Toast 的回調
    private var forceRemoveListener: (() -> Unit)? = null
    private var onClickListener: (() -> Unit)? = null

    abstract fun onTouch(view: View, event: MotionEvent): Boolean
    abstract fun canAutoRemove(): Boolean
    fun setForceRemoveListener(listener: () -> Unit) {
        forceRemoveListener = listener
    }
    protected fun forceRemove() {
        forceRemoveListener?.invoke()
    }

    fun setOnClickListener(listener: () -> Unit) {
        onClickListener = listener
    }

    @CallSuper
    protected open fun onClick(view: View) {
        onClickListener?.invoke()
    }
}