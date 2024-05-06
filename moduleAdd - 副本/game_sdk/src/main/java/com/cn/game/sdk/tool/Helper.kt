package com.cn.game.sdk.tool

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cn.game.sdk.R
import com.cn.game.sdk.utils.GamePartyLibraryInitializer
import com.xcjh.base_lib.utils.SpanUtil

/**
 * 任务完成提示
 */
fun myToast(whiteStr: String?, yellowStr: String? = null) {
    val view: View = LayoutInflater.from(GamePartyLibraryInitializer.mAppContext).inflate(R.layout.view_toast_my_task, null)
    val tvMsg = view.findViewById<View>(R.id.tvToast) as TextView
    SpanUtil.create()
        .addForeColorSection(whiteStr, ContextCompat.getColor(tvMsg.context, R.color.white))
        .addForeColorSection(
            yellowStr ?: "",
            ContextCompat.getColor(tvMsg.context, R.color.successColor)
        )
        .showIn(tvMsg) //显示到控件TextView中
    val toast = Toast(GamePartyLibraryInitializer.mAppContext)
    // toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, DisplayUtils.dp2px(50f))
    toast.setGravity( Gravity.CENTER, 0,0)
//    toast.duration = Toast.LENGTH_LONG
    toast.duration = Toast.LENGTH_SHORT
    toast.view = view
    toast.show()
}

