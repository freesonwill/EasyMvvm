package com.cn.game.sdk2.utils.ext

import android.graphics.Paint
import android.widget.TextView

/**
 * 文字加粗无效的时候，如： textView.setTypeface(null, Typeface.BOLD) 或者 textView.typeface = Typeface.DEFAULT_BOLD
 */
fun TextView?.setTextBold(isBold: Boolean) {
    try {
        if (this != null) {
            val paint: Paint? = this.paint
            paint?.isFakeBoldText = isBold
        }
    } catch (_: Exception) {
    }
}