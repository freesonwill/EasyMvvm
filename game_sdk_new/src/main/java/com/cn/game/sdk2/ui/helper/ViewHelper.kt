package com.cn.game.sdk2.ui.helper

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import com.cn.game.sdk2.R
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.SidePattern

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 17:43
 **/
object ViewHelper {
    private const val TAG_1 = "TAG_1"

    fun showFastView(context: Context) {
        EasyFloat.with(context).setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setTag(TAG_1)
            .setGravity(Gravity.END, 0, 300)
            .setLayout(R.layout.drag_fast_easy) {
                val llFastClick = it.findViewById<LinearLayout>(R.id.llFastClick)
                llFastClick.setOnClickListener {
                    Toast.makeText(context, "hello", Toast.LENGTH_LONG).show()
                    EasyFloat.hide(TAG_1)
                }
            }.show()
    }

}