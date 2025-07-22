package arch.cayenne.lib.common.utils.helper.toastGesture

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper

abstract class ToastGesture {

    // 用於強制移除 Toast 的回調
    private var forceRemoveListener: (() -> Unit)? = null
    private var onClickListener: ((v: View) -> Unit)? = null

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    open fun setGesture(view: View) {
        view.setOnTouchListener { v, event ->
            this.onTouch(v, event)
        }
        view.setOnClickListener { v ->
            this.onClick(v)
        }
    }

    @CallSuper
    open fun clearGesture(view: View) {
        view.setOnTouchListener(null)
        view.setOnClickListener(null)
    }

    abstract fun onTouch(view: View, event: MotionEvent): Boolean

    /**
     * 攔截刪除toast事件
     * @return 返回 false 代表攔截事件, 若攔截則需手動調用forceRemove移除toast
     */
    abstract fun canAutoRemove(): Boolean

    fun setForceRemoveListener(listener: () -> Unit) {
        forceRemoveListener = listener
    }

    protected fun forceRemove() {
        forceRemoveListener?.invoke()
    }

    fun setOnClickListener(listener: (v: View) -> Unit) {
        onClickListener = listener
    }

    @CallSuper
    protected open fun onClick(view: View) {
        onClickListener?.invoke(view)
    }
}