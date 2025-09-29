package arch.cayenne.module.chat.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


object EditTextUtils {

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(context: Activity?, editText: EditText) {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /**
     * 显示软键盘
     */
    fun showKeyboard(context: Activity?, editText: EditText) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, 0)
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
//        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//        editText.requestFocus()

    }



}