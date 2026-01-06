package arch.cayenne.module.chat.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd


object EditTextUtils {

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(context: Context?, editText: EditText) {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /**
     * 显示软键盘
     */
    fun showKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var flag =  imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        if(!flag){
            editText.postDelayed({
            flag = imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            if(!flag){
                showKeyboard(context, editText)
            }
            },50)
        }
        "showSoftInput result: $flag  isShown ${editText.isShown}  ${editText.hasWindowFocus()} ".logd("aaa")
//        imm.toggleSoftInput(0, 0)
//        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//        editText.requestFocus()

    }

    /**
     * 是否打开软键盘
     * @param context
     * @return
     */
    fun isSoftInputShown(context :Context) :Boolean{
        val decorView: View = (context as Activity).window.decorView
        val screenHeight:Int = decorView.height
        val rect = Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom - getNavigateBarHeight(context) > 0;
    }
    // 兼容有导航键的情况
    fun getNavigateBarHeight(context:Context): Int {
        val metrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }


    fun EditText.editDelBtn(){
        // 创建完整参数的 KeyEvent
        val keyEventDown = KeyEvent(
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            KeyEvent.ACTION_DOWN,
            KeyEvent.KEYCODE_DEL,
            0,
            0,
            KeyCharacterMap.VIRTUAL_KEYBOARD,
            0,
            KeyEvent.FLAG_FROM_SYSTEM,
            InputDevice.SOURCE_KEYBOARD
        )
        this.dispatchKeyEvent(keyEventDown)
    }


}