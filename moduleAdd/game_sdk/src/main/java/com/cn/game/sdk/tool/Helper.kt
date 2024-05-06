package com.cn.game.sdk.tool

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cn.game.sdk.R
import com.cn.game.sdk.utils.GamePartyLibraryInitializer
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.SpanUtil

/**
 * 任务完成提示
 * isDeep是否是深色模式，默认是浅色，当isDeep=true的时候是深色界面使用
 */
@SuppressLint("WrongConstant", "MissingInflatedId")
fun myToast(whiteStr: String?, yellowStr: String? = null,isDeep:Boolean=false,gravity:Int=Gravity.CENTER) {
    Handler(Looper.getMainLooper()).post {
        val view: View = LayoutInflater.from(appContext)
            .inflate(com.xcjh.base_lib.R.layout.view_toast_my_task, null)
        val tvMsg = view.findViewById<View>(com.xcjh.base_lib.R.id.tvToast) as TextView
        val llToastBe = view.findViewById<View>(com.xcjh.base_lib.R.id.llToastBe) as LinearLayout
        var txtColor = ContextCompat.getColor(tvMsg.context, com.xcjh.base_lib.R.color.white)
        if (isDeep) {
            txtColor = ContextCompat.getColor(tvMsg.context, com.xcjh.base_lib.R.color.white)
            llToastBe.background = ContextCompat.getDrawable(
                llToastBe.context,
                com.xcjh.base_lib.R.drawable.shape_4_ffffff
            )
        }
        SpanUtil.create()
            .addForeColorSection(whiteStr, txtColor)
            .addForeColorSection(
                yellowStr ?: "",
                ContextCompat.getColor(tvMsg.context, com.xcjh.base_lib.R.color.successColor)
            )
            .showIn(tvMsg) //显示到控件TextView中
        val toast = Toast(appContext)
        // toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, DisplayUtils.dp2px(50f))
        toast.setGravity(gravity, 0, 200)
        toast.duration = 5000
        toast.view = view
        toast.show()
    }
}

