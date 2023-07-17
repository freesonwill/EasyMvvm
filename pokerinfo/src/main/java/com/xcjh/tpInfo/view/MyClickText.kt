package com.xcjh.tpInfo.view

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.xcjh.tpInfo.R

/**
 * 可点击的文本
 */
class MyClickText(private var context: Context, private val txColor:Int= R.color.c_4b2196, private var callback: () -> Unit) : ClickableSpan() {
    override fun updateDrawState(ds:TextPaint){
        super.updateDrawState(ds)
        //设置文本的颜色
        ds.color = getColor(context,txColor)
        //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
        ds.isUnderlineText = false
    }
    override fun onClick(widget: View) {
        //点击事件
        callback.invoke()
    }
}