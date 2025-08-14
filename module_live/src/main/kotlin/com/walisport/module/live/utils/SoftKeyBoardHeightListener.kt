package com.walisport.module.live.utils

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.WindowManager
import android.widget.PopupWindow
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import com.walisport.module.live.R
import com.walisport.module.live.ui.LiveSoftKeyboardFragment

/**
 * @author: wenxi
 * @date: 13/8/25 23:25
 * @description: 使用高度占满屏幕，宽度为0的popupwinwod测量软件盘高度，测出后自动关闭该popupwindow
 */
internal class SoftKeyBoardHeightListener(private val activity: Activity) : PopupWindow(activity) {
    private var mListener: KeyboardHeightListener? = null
    private var popupView: View? = null
    private var parentView: View? = null

    init {
        val inflator = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflator.inflate(R.layout.keyboard_popup_windows, null, false)
        contentView = popupView

        softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED

        parentView = activity.findViewById(android.R.id.content)
        "parentView ${parentView != null}".logd("aaa")
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        popupView?.viewTreeObserver?.addOnGlobalLayoutListener {
            if (popupView != null) {
                handleOnGlobalLayout()
            }
        }
    }

    fun start() {
//        parentView?.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
//            override fun onViewAttachedToWindow(view: View) {
//                "onViewAttached".logd("aaa")
//                if (!isShowing && parentView?.windowToken != null) {
//                    setBackgroundDrawable(ColorDrawable(0))
//                    showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
//                }
//            }
//
//            override fun onViewDetachedFromWindow(view: View) {
//                "onViewDetachedFromWindow".logd("aaa")
//
//            }
//        })

        setBackgroundDrawable(ColorDrawable(0))
        showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)

    }

    fun close() {
        this.mListener = null
        dismiss()
    }

    fun registerKeyboardHeightListener(listener: KeyboardHeightListener?) {
        this.mListener = listener
    }

    private fun handleOnGlobalLayout() {
        val screenSize = Point()
        activity.windowManager.defaultDisplay.getSize(screenSize)

        val rect = Rect()
        popupView!!.getWindowVisibleDisplayFrame(rect)

        val keyboardHeight = screenSize.y - rect.bottom

        notifyKeyboardHeightChanged(keyboardHeight)
    }


    private fun notifyKeyboardHeightChanged(height: Int) {
        if (mListener != null) {
            mListener!!.onKeyboardHeightChanged(height)
        }
    }

    interface KeyboardHeightListener {
        fun onKeyboardHeightChanged(height: Int)
    }
}