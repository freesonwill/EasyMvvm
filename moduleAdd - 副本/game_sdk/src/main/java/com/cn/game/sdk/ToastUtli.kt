package com.cn.game.sdk

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.cn.game.sdk.ui.CeshiSdkActivity

class ToastUtli {

    fun showToast(name:String,context: Context){
        Toast.makeText(context,name, Toast.LENGTH_LONG).show()
    }

    fun showActivity(context:Context){
        var inten= Intent(context, CeshiSdkActivity::class.java)
        context.startActivity(inten)
    }
}