package com.walisport.module.live.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import java.util.LinkedList

class SoftKeyboardStateHelper @JvmOverloads constructor(
    private val activityRootView: View,
    var isSoftKeyboardOpened: Boolean = false
) :
    OnGlobalLayoutListener {
    interface SoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeightInPx: Int)

        fun onSoftKeyboardClosed()
    }

    private val listeners: MutableList<SoftKeyboardStateListener> = LinkedList()

    /**
     * Default value is zero (0)
     *
     * @return last saved keyboard height in px
     */
    var lastSoftKeyboardHeightInPx: Int = 0
        private set

    init {
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val r = Rect()
        // r will be populated with the coordinates of your view that area still
        // visible.
        activityRootView.getWindowVisibleDisplayFrame(r)

        val heightDiff = (activityRootView.rootView.height
                - (r.bottom - r.top))
        if (!isSoftKeyboardOpened && heightDiff > 100) { // if more than 100
            // pixels, its probably
            // a keyboard...
            isSoftKeyboardOpened = true
            notifyOnSoftKeyboardOpened(heightDiff)
        } else if (isSoftKeyboardOpened && heightDiff < 100) {
            isSoftKeyboardOpened = false
            notifyOnSoftKeyboardClosed()
        }
    }

    fun addSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.add(listener)
    }

    fun removeSoftKeyboardStateListener(
        listener: SoftKeyboardStateListener
    ) {
        listeners.remove(listener)
    }

    private fun notifyOnSoftKeyboardOpened(keyboardHeightInPx: Int) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx

        for (listener in listeners) {
            listener?.onSoftKeyboardOpened(keyboardHeightInPx)
        }
    }

    private fun notifyOnSoftKeyboardClosed() {
        for (listener in listeners) {
            listener?.onSoftKeyboardClosed()
        }
    }
}
