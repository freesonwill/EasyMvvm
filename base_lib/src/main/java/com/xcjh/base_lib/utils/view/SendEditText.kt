package com.xcjh.base_lib.utils.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText


/**
 * 自定义多行输入
 * imeOptions actionSend
 */
@SuppressLint("AppCompatCustomView")
class SendEditText : EditText {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val connection = super.onCreateInputConnection(outAttrs)
     /*   val imeActions = outAttrs.imeOptions and EditorInfo.IME_MASK_ACTION
        if (imeActions and EditorInfo.IME_ACTION_SEND != 0) {
            // 清除现有操作
            outAttrs.imeOptions = outAttrs.imeOptions xor imeActions
            // 设置 DONE 动作
            outAttrs.imeOptions = outAttrs.imeOptions or EditorInfo.IME_ACTION_SEND
        }
        if (outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION != 0) {
            outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        }*/
        if (connection != null) {
            outAttrs.imeOptions = outAttrs.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION.inv()
        }
        return connection
    }
}