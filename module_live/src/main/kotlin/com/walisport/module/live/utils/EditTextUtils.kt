package com.walisport.module.live.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object EditTextUtils {

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(context: Context?, editText: EditText) {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
    }

    /**
     * 显示软键盘
     */
    fun showKeyboard(context: Context?, editText: EditText) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        editText.requestFocus()
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(context: Context?) {
        val activity = context as Activity?
        if (activity != null) {
            val imm = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive && activity.currentFocus != null) {
                imm.hideSoftInputFromWindow(
                    activity.currentFocus!!
                        .windowToken, 0
                )
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setHideSoftKeyBoard(editText: EditText) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            editText.showSoftInputOnFocus = false
//        } else {
//            try {
//                val method =
//                    EditText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.java)
//                method.isAccessible = true
//                method.invoke(editText, false)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        editText.inputType = InputType.TYPE_NULL
        editText.keyListener = null
    }


}