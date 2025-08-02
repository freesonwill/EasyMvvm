package com.walisport.module.live.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


object EditTextUtils {

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(context: Activity?, editText: EditText) {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
//        var view: View? = context?.currentFocus
//        if (view == null) view = View(context)
//        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 显示软键盘
     */
    fun showKeyboard(context: Activity?, editText: EditText) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
//        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }


}