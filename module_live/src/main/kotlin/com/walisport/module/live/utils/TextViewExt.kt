package com.walisport.module.live.utils

import android.graphics.drawable.Drawable
import android.widget.TextView

object TextViewExt {

    fun TextView.setBottomDrawable(drawable: Drawable?,drawablePadding: Int) {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(
            drawables[0], // left
            drawables[1], // top
            drawables[2], // right
            drawable      // bottom
        )
        compoundDrawablePadding = drawablePadding // 设置图片与文字的间距
    }

    fun TextView.clearBottomDrawable() {
        val drawables = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(
            drawables[0], // left
            drawables[1], // top
            drawables[2], // right
            null          // bottom
        )
    }
}